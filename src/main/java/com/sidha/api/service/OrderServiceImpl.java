package com.sidha.api.service;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sidha.api.DTO.request.order.CreateOrderRequestDTO;
import com.sidha.api.DTO.request.order.OrderConfirmRequestDTO;
import com.sidha.api.DTO.request.order.CreateOrderItemRequestDTO;
import com.sidha.api.DTO.request.order.CreateRuteRequestDTO;
import com.sidha.api.DTO.request.order.UpdateOrderRequestDTO;
import com.sidha.api.model.PenawaranHargaItem;
import com.sidha.api.model.enumerator.TipeBarang;
import com.sidha.api.model.enumerator.TipeTruk;
import com.sidha.api.model.order.Order;
import com.sidha.api.model.order.OrderItem;
import com.sidha.api.model.order.OrderItemHistory;
import com.sidha.api.model.order.Rute;
import com.sidha.api.model.user.Klien;
import com.sidha.api.repository.OrderDb;
import com.sidha.api.repository.OrderItemDb;
import com.sidha.api.repository.OrderItemHistoryDb;
import com.sidha.api.repository.RuteDb;
import java.util.NoSuchElementException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.modelmapper.ModelMapper;
import com.sidha.api.model.image.BongkarMuatImage;
import com.sidha.api.model.image.ImageData;
import com.sidha.api.repository.ImageDataDb;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private OrderDb orderDb;
    private OrderItemDb orderItemDb;
    private RuteDb ruteDb;
    private UserService userService;
    private StorageService storageService;
    private ImageDataDb imageDataDb;
    private ModelMapper modelMapper;

    private InvoiceService invoiceService;
    private OrderItemHistoryDb orderItemHistoryDb;

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public Order createOrder(CreateOrderRequestDTO request) {
        var user = userService.findById(request.getKlienId());
        var klien = (Klien) user;

        var order = new Order();
        order.setKlien(klien);
        order.setTanggalPengiriman(request.getTanggalPengiriman());
        orderDb.save(order);

        var orderItems = new ArrayList<OrderItem>();
        request.getOrderItems().forEach(item -> {
            var orderItemSaved = saveOrderItem(item, order, klien);
            orderItems.add(orderItemSaved);
        });

        order.setOrderItems(orderItems);
        order.setTotalPrice(orderItems.stream().mapToLong(OrderItem::getPrice).sum());
        var invoice = invoiceService.createInvoice();
        order.setInvoice(invoice);
        invoice.setOrder(order);
        invoiceService.saveInvoice(invoice);

        return orderDb.save(order);
    }

    private OrderItem saveOrderItem(CreateOrderItemRequestDTO request, Order order, Klien klien) {
        var orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setIsPecahBelah(request.getIsPecahBelah());
        orderItem.setStatusOrder(0);
        orderItem.setTipeBarang(TipeBarang.valueOf(request.getTipeBarang()));
        orderItem.setTipeTruk(TipeTruk.valueOf(request.getTipeTruk()));
        orderItemDb.save(orderItem);

        var rute = new ArrayList<Rute>();
        request.getRute().forEach(r -> {
            var ruteSaved = saveRute(r, orderItem, klien);
            rute.add(ruteSaved);
        });
        orderItem.setRute(rute);

        var orderItemHistories = new ArrayList<OrderItemHistory>();
        orderItemHistories.add(addOrderItemHistory(orderItem, 0, "Order berhasil dibuat", klien.getUsername()));
        orderItem.setOrderItemHistories(orderItemHistories);

        orderItem.setPrice(rute.stream().mapToLong(Rute::getPrice).sum());
        return orderItemDb.save(orderItem);
    }

    private Rute saveRute(CreateRuteRequestDTO request, OrderItem orderItem, Klien klien) {
        var rute = new Rute();
        rute.setOrderItem(orderItem);
        rute.setAlamatPengiriman(request.getAlamatPengiriman());
        rute.setAlamatPenjemputan(request.getAlamatPenjemputan());
        rute.setDestination(request.getDestination());
        rute.setSource(request.getSource());
        rute.setPrice(getPriceRute(orderItem.getTipeTruk(), request.getSource(), request.getDestination(),
                klien.getListPenawaranHargaItem()));

        return ruteDb.save(rute);
    }

    private Integer getPriceRute(TipeTruk tipeTruk, String source, String destination,
            List<PenawaranHargaItem> listPenawaranHargaItem) {
        for (PenawaranHargaItem penawaranHargaItem : listPenawaranHargaItem) {
            if (penawaranHargaItem.getDestination().equals(destination)
                    && penawaranHargaItem.getSource().equals(source)) {
                switch (tipeTruk) {
                    case CDD:
                        return penawaranHargaItem.getCddPrice();
                    case CDL:
                        return penawaranHargaItem.getCddLongPrice();
                    case Wingbox:
                        return penawaranHargaItem.getWingboxPrice();
                    case Fuso:
                        return penawaranHargaItem.getFusoPrice();
                    default:
                        throw new IllegalArgumentException("Tipe Truk not found");
                }
            }
        }
        throw new IllegalArgumentException("Penawaran Harga not found");
    }

    @Override
    public List<Order> getOrdersByKlienId(UUID klienId) {
        return orderDb.findByKlienId(klienId);
    }

    @Override
    public Order updateOrder(UpdateOrderRequestDTO request) {
        var order = orderDb.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setTanggalPengiriman(request.getTanggalPengiriman());

        request.getOrderItems().forEach(item -> {
            if (item.getOrderItemId() != null) { // Jika order item sudah ada
                var orderItem = orderItemDb.findById(item.getOrderItemId())
                        .orElseThrow(() -> new IllegalArgumentException("Order Item not found"));
                orderItem.setIsPecahBelah(item.getIsPecahBelah());
                orderItem.setTipeBarang(TipeBarang.valueOf(item.getTipeBarang()));
                orderItem.setTipeTruk(TipeTruk.valueOf(item.getTipeTruk()));
                orderItem.setKeterangan(item.getKeterangan());

                item.getRute().forEach(r -> {
                    if (r.getRuteId() != null) { // Jika rute sudah ada
                        var rute = ruteDb.findById(r.getRuteId())
                                .orElseThrow(() -> new IllegalArgumentException("Rute not found"));
                        rute.setSource(r.getSource());
                        rute.setDestination(r.getDestination());
                        rute.setAlamatPengiriman(r.getAlamatPengiriman());
                        rute.setAlamatPenjemputan(r.getAlamatPenjemputan());

                        rute.setPrice(getPriceRute(orderItem.getTipeTruk(), r.getSource(), r.getDestination(),
                                order.getKlien().getListPenawaranHargaItem()));
                        ruteDb.save(rute);

                    } else { // Jika menambahkan rute baru
                        CreateRuteRequestDTO newRute = new CreateRuteRequestDTO();
                        newRute.setSource(r.getSource());
                        newRute.setDestination(r.getDestination());
                        newRute.setAlamatPengiriman(r.getAlamatPengiriman());
                        newRute.setAlamatPenjemputan(r.getAlamatPenjemputan());
                        var ruteSaved = saveRute(newRute, orderItem, order.getKlien());
                        orderItem.getRute().add(ruteSaved);
                    }
                });

                orderItem.setPrice(orderItem.getRute().stream().mapToLong(Rute::getPrice).sum());
                orderItemDb.save(orderItem);

            } else { // Jika menambahkan order item baru
                CreateOrderItemRequestDTO newItem = new CreateOrderItemRequestDTO();
                newItem.setIsPecahBelah(item.getIsPecahBelah());
                newItem.setTipeBarang(item.getTipeBarang());
                newItem.setTipeTruk(item.getTipeTruk());
                newItem.setKeterangan(item.getKeterangan());
                newItem.setRute(new ArrayList<>());

                var orderItemSaved = saveOrderItem(newItem, order, order.getKlien());
                newItem.getRute().forEach(r -> {
                    CreateRuteRequestDTO newRute = new CreateRuteRequestDTO();
                    newRute.setAlamatPengiriman(r.getAlamatPengiriman());
                    newRute.setAlamatPenjemputan(r.getAlamatPenjemputan());
                    newRute.setDestination(r.getDestination());
                    newRute.setSource(r.getSource());

                    var ruteSaved = saveRute(newRute, orderItemSaved, order.getKlien());
                    orderItemSaved.getRute().add(ruteSaved);
                });

                orderItemSaved.setPrice(orderItemSaved.getRute().stream().mapToLong(Rute::getPrice).sum());
                orderItemDb.save(orderItemSaved);
                order.getOrderItems().add(orderItemSaved);
            }

        });

        order.setTotalPrice(order.getOrderItems().stream().mapToLong(OrderItem::getPrice).sum());
        return orderDb.save(order);
    }

    @Override
    public Order confirmOrder(OrderConfirmRequestDTO request) {
        try {
            for (var confirmOrderItem : request.getOrderItems()) {
                var orderItem = orderItemDb.findById(confirmOrderItem.getOrderItemId())
                        .orElseThrow(() -> new IllegalArgumentException("Order Item not found"));

                if (orderItem.getStatusOrder() != 0) {
                    throw new IllegalArgumentException("Order Item already confirmed");
                }

                if (confirmOrderItem.getIsAccepted()) {
                    orderItem.setStatusOrder(1);
                } else {
                    orderItem.setStatusOrder(-1);
                    orderItem.setAlasanPenolakan(confirmOrderItem.getRejectionReason());
                }

                var createdBy = userService.findById(request.getKaryawanId()).getUsername();
                var orderItemHistory = addOrderItemHistory(orderItem, orderItem.getStatusOrder(),
                        confirmOrderItem.getIsAccepted() ? "Order diterima"
                                : "Order ditolak: " + confirmOrderItem.getRejectionReason(),
                        createdBy);

                orderItem.getOrderItemHistories().add(orderItemHistory);
                orderItemDb.save(orderItem);

                logger.info("Order item {} processed with status {}", orderItem.getId(), orderItem.getStatusOrder());
            }
            return orderDb.findById(request.getOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        } catch (Exception e) {
            logger.error("Error confirming order: {}", e.getMessage(), e);
            throw e;
        }

    }

    @Override
    public List<Order> getAllOrders() {
        return orderDb.findAll();
    }

    @Override
    public OrderItem findOrderItemById(UUID idOrderItem) {
        OrderItem orderItem = orderItemDb.findById(idOrderItem).orElse(null);

        if (orderItem == null) {
            throw new NoSuchElementException("Id invoice tidak valid");
        }

        return orderItem;
    }

    @Override
    public OrderItem saveImageBongkarMuat(OrderItem orderItem) {
        return orderItemDb.save(orderItem);
    }

    @Override
    public OrderItem uploadImageMuat(UUID idOrderItem, MultipartFile imageFile) throws IOException {
        OrderItem orderItem = this.findOrderItemById(idOrderItem);
        ImageData imageData = storageService.uploadImageAndSaveToDB(
                imageFile,
                imageFile.getOriginalFilename());
        BongkarMuatImage muatImage = modelMapper.map(imageData, BongkarMuatImage.class);

        ImageData currentImage;
        currentImage = orderItem.getBuktiMuat();
        orderItem.setBuktiMuat(muatImage);
        this.saveImageBongkarMuat(orderItem);

        if (currentImage != null) {
            storageService.deleteImageFile(currentImage);
            imageDataDb.delete(currentImage);
        }
        muatImage.setOrderItem(orderItem);
        imageDataDb.save(muatImage);
        return orderItem;
    }

    @Override
    public OrderItem uploadImageBongkar(UUID idOrderItem, MultipartFile imageFile) throws IOException {
        OrderItem orderItem = this.findOrderItemById(idOrderItem);
        ImageData imageData = storageService.uploadImageAndSaveToDB(
                imageFile,
                imageFile.getOriginalFilename());
        BongkarMuatImage bongkarImage = modelMapper.map(imageData, BongkarMuatImage.class);

        ImageData currentImage;
        currentImage = orderItem.getBuktiBongkar();
        orderItem.setBuktiBongkar(bongkarImage);
        this.saveImageBongkarMuat(orderItem);

        if (currentImage != null) {
            storageService.deleteImageFile(currentImage);
            imageDataDb.delete(currentImage);
        }
        bongkarImage.setOrderItem(orderItem);
        imageDataDb.save(bongkarImage);
        return orderItem;
    }

    @Override
    public ImageData getImageMuat(UUID idOrderItem) {
        OrderItem orderItem = this.findOrderItemById(idOrderItem);
        ImageData imageData = orderItem.getBuktiMuat();
        return imageData;
    }

    @Override
    public ImageData getImageBongkar(UUID idOrderItem) {
        OrderItem orderItem = this.findOrderItemById(idOrderItem);
        ImageData imageData = orderItem.getBuktiBongkar();
        return imageData;
    }

    @Override
    public void deleteImageMuat(UUID idOrderItem) {
        ImageData imageData = this.getImageMuat(idOrderItem);
        if (imageData != null) {
            OrderItem orderItem = this.findOrderItemById(idOrderItem);
            orderItem.setBuktiMuat(null);
            this.saveImageBongkarMuat(orderItem);

            storageService.deleteImageFile(imageData);
            imageDataDb.delete(imageData);
        } else {
            throw new NoSuchElementException("Belum ada bukti yang diunggah");
        }
    }

    @Override
    public void deleteImageBongkar(UUID idOrderItem) {
        ImageData imageData = this.getImageBongkar(idOrderItem);
        if (imageData != null) {
            OrderItem orderItem = this.findOrderItemById(idOrderItem);
            orderItem.setBuktiBongkar(null);
            this.saveImageBongkarMuat(orderItem);

            storageService.deleteImageFile(imageData);
            imageDataDb.delete(imageData);
        } else {
            throw new NoSuchElementException("Belum ada bukti yang diunggah");
        }
    }

    @Override
    public List<OrderItem> getAllOrderItemByIdSopir(UUID sopir){
        return orderItemDb.findByIdSopir(sopir);
    }

    @Override
    public OrderItem getOrderItemById(UUID idOrderItem) {
        OrderItem orderItem = orderItemDb.findById(idOrderItem).orElse(null);
        if (orderItem == null) {
            throw new NoSuchElementException("Id order tidak valid");
        }
        return orderItem;
    }

    @Override
    public List<OrderItem> getAllOrderItemByIdOrder(UUID idOrder){
        return orderItemDb.findByIdOrder(idOrder);
    }
    private OrderItemHistory addOrderItemHistory(OrderItem orderItem, Integer status, String description,
            String createdBy) {
        var orderItemHistory = new OrderItemHistory();
        orderItemHistory.setOrderItem(orderItem);
        orderItemHistory.setStatus(status);
        orderItemHistory.setDescription(description);
        orderItemHistory.setCreatedBy(createdBy);
        return orderItemHistoryDb.save(orderItemHistory);
    }

    @Override
    public Order getOrderById(UUID orderId) {
        return orderDb.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Override
    public Order getPrice(CreateOrderRequestDTO request) {
        var user = userService.findById(request.getKlienId());
        var klien = (Klien) user;

        var order = new Order();
        order.setKlien(klien);

        var orderItems = new ArrayList<OrderItem>();
        request.getOrderItems().forEach(item -> {
            var orderItem = new OrderItem();
            orderItem.setIsPecahBelah(item.getIsPecahBelah());
            orderItem.setTipeBarang(TipeBarang.valueOf(item.getTipeBarang()));
            orderItem.setTipeTruk(TipeTruk.valueOf(item.getTipeTruk()));

            var rute = new ArrayList<Rute>();
            item.getRute().forEach(r -> {
                var ruteItem = new Rute();
                ruteItem.setSource(r.getSource());
                ruteItem.setDestination(r.getDestination());
                ruteItem.setAlamatPengiriman(r.getAlamatPengiriman());
                ruteItem.setAlamatPenjemputan(r.getAlamatPenjemputan());
                ruteItem.setPrice(getPriceRute(orderItem.getTipeTruk(), r.getSource(), r.getDestination(),
                        klien.getListPenawaranHargaItem()));
                rute.add(ruteItem);
            });

            orderItem.setRute(rute);
            orderItem.setPrice(rute.stream().mapToLong(Rute::getPrice).sum());
            orderItems.add(orderItem);
        });

        order.setOrderItems(orderItems);
        order.setTotalPrice(orderItems.stream().mapToLong(OrderItem::getPrice).sum());
        return order;
    }

    @Override
    public List<String> getAllPossibleRute(UUID userId) {
        // list["source - destinantion"]
        List<String> listRute = new ArrayList<>();
        var listPenawaranHargaItem = ((Klien) userService.findById(userId)).getListPenawaranHargaItem();
        for (PenawaranHargaItem penawaranHargaItem : listPenawaranHargaItem) {
            listRute.add(penawaranHargaItem.getSource() + " - " + penawaranHargaItem.getDestination());
        }
        return listRute;
    }

}
