package com.sidha.api.service;

import com.sidha.api.DTO.request.KonfirmasiBuktiPembayaranDTO;
import com.sidha.api.model.Invoice;
import com.sidha.api.model.image.ImageData;
import com.sidha.api.model.image.InvoiceImage;
import com.sidha.api.repository.ImageDataDb;
import com.sidha.api.repository.InvoiceDb;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private InvoiceDb invoiceDb;

    private StorageService storageService;

    private ImageDataDb imageDataDb;

    private ModelMapper modelMapper;


    @Override
    public Invoice saveInvoice(Invoice invoice) {
        return invoiceDb.save(invoice);
    }

    @Override
    public Invoice findInvoiceById(UUID idInvoice) {
        Invoice invoice = invoiceDb.findById(idInvoice).orElse(null);

        if (invoice == null) {
            throw new NoSuchElementException("Id invoice tidak valid");
        }

        return invoice;
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
        if (this.getImageBuktiPembayaran(idInvoice, isPelunasan) != null) {
            this.deleteImageBuktiPembayaran(idInvoice, isPelunasan);
        }

        ImageData imageData = storageService.uploadImageAndSaveToDB(
                imageFile,
                idInvoice + "_" + isPelunasan + "_" + imageFile.getOriginalFilename());
        InvoiceImage invoiceImage = modelMapper.map(imageData, InvoiceImage.class);

        if (!isPelunasan) {
            invoice.setBuktiDp(invoiceImage);
        } else {
            invoice.setBuktiPelunasan(invoiceImage);
        }
        this.saveInvoice(invoice);

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
        ImageData imageData = this.getImageBuktiPembayaran(
                idInvoice,
                konfirmasiBuktiPembayaranDTO.getIsPelunasan()
        );

        InvoiceImage invoiceImage = modelMapper.map(imageData, InvoiceImage.class);

        if (konfirmasiBuktiPembayaranDTO.getIsConfirmed()) {
            invoiceImage.setStatus(1);
        } else {
            String alasanPenolakan = konfirmasiBuktiPembayaranDTO.getAlasanPenolakan();
            if (alasanPenolakan == null) {
                throw new RuntimeException("Alasan penolakan tidak boleh kosong");
            }
            invoiceImage.setStatus(2);
            invoiceImage.setAlasanPenolakan(alasanPenolakan);
        }
        imageDataDb.save(invoiceImage);

        return this.findInvoiceById(idInvoice);
    }


}
