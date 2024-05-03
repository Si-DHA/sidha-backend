package com.sidha.api.service;

import com.sidha.api.DTO.request.KonfirmasiBuktiPembayaranDTO;
import com.sidha.api.model.Invoice;
import com.sidha.api.model.image.ImageData;
import com.sidha.api.model.image.InvoiceImage;
import com.sidha.api.model.order.Order;
import com.sidha.api.model.order.OrderItem;
import com.sidha.api.repository.ImageDataDb;
import com.sidha.api.repository.InvoiceDb;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private InvoiceDb invoiceDb;

    private StorageService storageService;

    private ImageDataDb imageDataDb;

    private ModelMapper modelMapper;

    private OrderService orderService;


    @Override
    public Invoice saveInvoice(Invoice invoice) {
        return invoiceDb.save(invoice);
    }

    @Override
    public Invoice findInvoiceById(UUID idInvoice) {
        return invoiceDb.findById(idInvoice)
                .orElseThrow(() -> new NoSuchElementException("Id invoice tidak valid"));
    }

    @Override
    public List<Invoice> findInvoiceByIdKlien(UUID idKlien) {
        List<Invoice> listInvoiceKlien = new ArrayList<>();
        for (Invoice invoice : this.findAllInvoice()) {
            try {
                Order order = invoice.getOrder();
                if (order.getKlien().getId().equals(idKlien)) {
                    List<OrderItem> orderItems = order.getOrderItems();
                    boolean excludeInvoice = false;
                    boolean allItemsHaveStatusMinus1 = true;

                    // Check the status of each order item
                    for (OrderItem orderItem : orderItems) {
                        int status = orderItem.getStatusOrder();

                        // Check if there is any OrderItem with status 0 or 1
                        if (status == 0 || status == 1) {
                            excludeInvoice = true;
                            break;
                        }

                        // Check if all OrderItems have status -1
                        if (status != -1) {
                            allItemsHaveStatusMinus1 = false;
                        }
                    }

                    // Exclude the invoice if either condition is met
                    if (!excludeInvoice && !allItemsHaveStatusMinus1) {
                        listInvoiceKlien.add(invoice);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        if (!listInvoiceKlien.isEmpty()) {
            return listInvoiceKlien;
        } else {
            throw new NoSuchElementException("Klien belum memiliki invoice");
        }
    }

    @Override
    public List<Invoice> findAllInvoice() {
        return invoiceDb.findAll();
    }

    @Override
    public void deleteInvoiceById(UUID idInvoice) {
        this.findInvoiceById(idInvoice);
        invoiceDb.deleteById(idInvoice);
    }

    @Override
    public Invoice createInvoice() {
        Invoice invoice = new Invoice();
        return this.saveInvoice(invoice);
    }

    @Override
    public Invoice uploadBuktiPembayaran(UUID idInvoice, boolean isPelunasan, MultipartFile imageFile) throws IOException {

        Invoice invoice = this.findInvoiceById(idInvoice);

        ImageData imageData = storageService.uploadImageAndSaveToDB(
                imageFile,
                idInvoice + "_" + isPelunasan + "_" + imageFile.getOriginalFilename());
        InvoiceImage invoiceImage = modelMapper.map(imageData, InvoiceImage.class);

        // order item history
        Order order = invoice.getOrder();
        List<OrderItem> orderItems = order.getOrderItems();
        String klien = order.getKlien().getCompanyName();
        String descriptionHistory;

        ImageData currentImage;
        if (!isPelunasan) {
            currentImage = invoice.getBuktiDp();
            invoice.setBuktiDp(invoiceImage);
            descriptionHistory = "bukti pembayaran DP";
        } else {
            currentImage = invoice.getBuktiPelunasan();
            invoice.setBuktiPelunasan(invoiceImage);
            descriptionHistory = "bukti pembayaran pelunasan";
        }
        this.saveInvoice(invoice);

        if (currentImage != null) {
            // order item history
            descriptionHistory = "Mengubah " + descriptionHistory;
            for (OrderItem orderItem : orderItems) {
                if (orderItem.getStatusOrder() >= 2) {
                    var orderItemHistories = orderItem.getOrderItemHistories();
                    orderItemHistories.add(
                            orderService.addOrderItemHistory(orderItem, descriptionHistory, klien)
                    );
                }
            }

            storageService.deleteImageFile(currentImage);
            imageDataDb.delete(currentImage);
        } else {
            // order item history
            descriptionHistory = "Mengunggah " + descriptionHistory;
            for (OrderItem orderItem : orderItems) {
                if (orderItem.getStatusOrder() >= 2) {
                    var orderItemHistories = orderItem.getOrderItemHistories();
                    orderItemHistories.add(
                            orderService.addOrderItemHistory(orderItem, descriptionHistory, klien)
                    );
                }
            }
        }

        invoiceImage.setInvoice(invoice);
        imageDataDb.save(invoiceImage);

        return invoice;
    }

    @Override
    public ImageData getImageBuktiPembayaran(UUID idInvoice, boolean isPelunasan) {
        Invoice invoice = this.findInvoiceById(idInvoice);

        ImageData imageData;
        if (!isPelunasan) {
            imageData = invoice.getBuktiDp();
        } else {
            imageData = invoice.getBuktiPelunasan();
        }

        return imageData;
    }

    @Override
    public void deleteImageBuktiPembayaran(UUID idInvoice, boolean isPelunasan) {
        ImageData imageData = this.getImageBuktiPembayaran(idInvoice, isPelunasan);
        if (imageData != null) {
            Invoice invoice = this.findInvoiceById(idInvoice);

            if (!isPelunasan) {
                invoice.setBuktiDp(null);
            } else {
                invoice.setBuktiPelunasan(null);
            }
            this.saveInvoice(invoice);

            storageService.deleteImageFile(imageData);
            imageDataDb.delete(imageData);
        } else {
            throw new NoSuchElementException("Belum ada bukti yang diunggah");
        }
    }

    @Override
    public Invoice konfirmasiBuktiPembayaran(KonfirmasiBuktiPembayaranDTO konfirmasiBuktiPembayaranDTO) {
        UUID idInvoice = konfirmasiBuktiPembayaranDTO.getIdInvoice();
        Invoice invoice = this.findInvoiceById(idInvoice);
        boolean isPelunasan = konfirmasiBuktiPembayaranDTO.getIsPelunasan();

        ImageData imageData = this.getImageBuktiPembayaran(
                idInvoice,
                isPelunasan
        );

        InvoiceImage invoiceImage = modelMapper.map(imageData, InvoiceImage.class);

        if (konfirmasiBuktiPembayaranDTO.getIsConfirmed()) {
            invoiceImage.setStatus(1);
            List<OrderItem> orderItems = invoice.getOrder().getOrderItems();

            for (OrderItem orderItem : orderItems) {
                if (orderItem.getStatusOrder() >= 0) {
                    if (isPelunasan) {
                        orderItem.setStatusOrder(5);
                    } else {
                        orderItem.setStatusOrder(3);
                    }
                }
            }
        } else {
            String alasanPenolakan = konfirmasiBuktiPembayaranDTO.getAlasanPenolakan();
            if (alasanPenolakan == null) {
                throw new RuntimeException("Alasan penolakan tidak boleh kosong");
            }
            invoiceImage.setStatus(-1);
            invoiceImage.setAlasanPenolakan(alasanPenolakan);
        }
        imageDataDb.save(invoiceImage);

        return invoice;
    }


}