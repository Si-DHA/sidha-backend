package com.sidha.api.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.Date;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sidha.api.DTO.request.order.CreateOrderItemRequestDTO;
import com.sidha.api.DTO.request.order.CreateOrderRequestDTO;
import com.sidha.api.DTO.request.order.CreateRuteRequestDTO;
import com.sidha.api.DTO.request.order.OrderConfirmRequestDTO;
import com.sidha.api.DTO.request.order.UpdateOrderRequestDTO;
import com.sidha.api.model.PenawaranHargaItem;
import com.sidha.api.model.enumerator.TipeBarang;
import com.sidha.api.model.enumerator.TipeTruk;
import com.sidha.api.model.image.BongkarMuatImage;
import com.sidha.api.model.image.ImageData;
import com.sidha.api.model.order.Order;
import com.sidha.api.model.order.OrderItem;
import com.sidha.api.model.order.OrderItemHistory;
import com.sidha.api.model.order.Rute;
import com.sidha.api.model.user.Klien;
import com.sidha.api.repository.ImageDataDb;
import com.sidha.api.repository.OrderDb;
import com.sidha.api.repository.OrderItemDb;
import com.sidha.api.repository.OrderItemHistoryDb;
import com.sidha.api.repository.RuteDb;

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

    private static int ORDER_NUMBER = 1;
    private static int ORDER_ITEM_NUMBER = 1;

    private String generateOrderId(CreateOrderRequestDTO request) {
        // OR-PTX-DDMMYY-0000
        String orderId = "OR-";

        String companyName = ((Klien) userService.findById(request.getKlienId())).getCompanyName().toUpperCase().strip();
        // remove whitespace and special characters
        companyName = companyName.replaceAll("[^a-zA-Z0-9]", "");
        if (companyName.substring(0,2).equals("PT") || companyName.substring(0,2).equals("CV")){
            companyName = companyName.substring(2);
        } 
        companyName = companyName.substring(0, Math.min(companyName.length(), 3));
        orderId += companyName + "-";

        Date date = request.getTanggalPengiriman();
        String dateStr = String.format("%td%tm%ty", date, date, date);
        orderId += dateStr + "-";

        String orderItemNumber = String.format("%04d", ORDER_NUMBER);
        orderId += orderItemNumber;
        ORDER_NUMBER++;

        return orderId;
    }

    private String generateOrderItemId(CreateOrderItemRequestDTO request, String orderId) {
        // ORI-PTX-CCD-DDMMYY-00000
        String orderItemId = "ORI-";

        String companyName = orderId.substring(3, 6);
        orderItemId += companyName + "-";

        String tipeTruk = request.getTipeTruk().toString().toUpperCase();
        orderItemId += tipeTruk + "-";

        String date = orderId.substring(7, 13);
        orderItemId += date + "-";

        String orderItemNumber = String.format("%05d", ORDER_ITEM_NUMBER);
        orderItemId += orderItemNumber;
        ORDER_ITEM_NUMBER++;

        return orderItemId;
    }

    @Override
    public Order createOrder(CreateOrderRequestDTO request) {
        var user = userService.findById(request.getKlienId());
        var klien = (Klien) user;

        var order = new Order();
        order.setKlien(klien);
        order.setTanggalPengiriman(request.getTanggalPengiriman());
        order.setId(generateOrderId(request));
        orderDb.save(order);

        var orderItems = new ArrayList<OrderItem>();
        request.getOrderItems().forEach(item -> {
            var orderItemSaved = saveOrderItem(item, order, klien);
            orderItems.add(orderItemSaved);
        });

        order.setOrderItems(orderItems);
        Long totalPrice = orderItems.stream().mapToLong(OrderItem::getPrice).sum();
        order.setTotalPrice(totalPrice);

        var invoice = invoiceService.createInvoice();
        order.setInvoice(invoice);
        invoice.setOrder(order);
        BigDecimal totalDp = BigDecimal.valueOf(0.6 * totalPrice);
        BigDecimal totalPelunasan = BigDecimal.valueOf(0.4 * totalPrice);;
        invoice.setTotalDp(totalDp);
        invoice.setTotalPelunasan(totalPelunasan);
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
        orderItem.setId(generateOrderItemId(request, order.getId()));
        orderItemDb.save(orderItem);

        var rute = new ArrayList<Rute>();
        request.getRute().forEach(r -> {
            var ruteSaved = saveRute(r, orderItem, klien);
            rute.add(ruteSaved);
        });
        orderItem.setRute(rute);

        var orderItemHistories = new ArrayList<OrderItemHistory>();
        orderItemHistories.add(this.addOrderItemHistory(orderItem, "Membentuk order item " + orderItem.getId(), klien.getCompanyName()));
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

        var klien = order.getKlien();

        request.getOrderItems().forEach(item -> {
            if (item.getOrderItemId() != null) { // Jika order item sudah ada
                var orderItem = orderItemDb.findById(item.getOrderItemId())
                        .orElseThrow(() -> new IllegalArgumentException("Order Item not found"));
                orderItem.setIsPecahBelah(item.getIsPecahBelah());
                orderItem.setTipeBarang(TipeBarang.valueOf(item.getTipeBarang()));
                orderItem.setTipeTruk(TipeTruk.valueOf(item.getTipeTruk()));
                orderItem.setKeterangan(item.getKeterangan());

                var orderItemHistories = orderItem.getOrderItemHistories();
                orderItemHistories.add(this.addOrderItemHistory(orderItem, "Memperbarui order item " + orderItem.getId(), klien.getCompanyName()));

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

        Long totalPrice = order.getOrderItems().stream().mapToLong(OrderItem::getPrice).sum();
        order.setTotalPrice(totalPrice);

        BigDecimal totalDp = BigDecimal.valueOf(0.6 * totalPrice);
        BigDecimal totalPelunasan = BigDecimal.valueOf(0.4 * totalPrice);
        var invoice = order.getInvoice();
        invoice.setTotalDp(totalDp);
        invoice.setTotalPelunasan(totalPelunasan);
        invoiceService.saveInvoice(invoice);

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

                if (!confirmOrderItem.getIsAccepted()) {
                    orderItem.setStatusOrder(-1);
                    orderItem.setAlasanPenolakan(confirmOrderItem.getRejectionReason());

                    Order order = this.getOrderByOrderItem(orderItem.getId());
                    var invoice = order.getInvoice();
                    Long orderPrice = order.getTotalPrice() - orderItem.getPrice();
                    Long dp = (long) (orderPrice * 0.6);
                    Long pelunasan = (long) (orderPrice * 0.4);
                    order.setTotalPrice(orderPrice);
                    invoice.setTotalDp(BigDecimal.valueOf(dp));
                    invoice.setTotalPelunasan(BigDecimal.valueOf(pelunasan));

                    var orderItemHistory = this.addOrderItemHistory(orderItem,
                            "Menolak order item dengan alasan \"" + confirmOrderItem.getRejectionReason() + "\"",
                            "PT DHA");
                    orderItem.getOrderItemHistories().add(orderItemHistory);
                }

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
    public OrderItem findOrderItemById(String idOrderItem) {
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
    public OrderItem uploadImageMuat(String idOrderItem, MultipartFile imageFile) throws IOException {
        OrderItem orderItem = this.findOrderItemById(idOrderItem);
        ImageData imageData = storageService.uploadImageAndSaveToDB(
                imageFile,
                imageFile.getOriginalFilename() + "_muat_" + idOrderItem
        );
        BongkarMuatImage muatImage = modelMapper.map(imageData, BongkarMuatImage.class);

        ImageData currentImage;
        currentImage = orderItem.getBuktiMuat();
        orderItem.setBuktiMuat(muatImage);
        this.saveImageBongkarMuat(orderItem);

        // order item history
        var orderItemHistories = orderItem.getOrderItemHistories();
        String sopir = "Sopir " + orderItem.getSopir().getName();
        String descriptionHistory;

        if (currentImage != null) {
            storageService.deleteImageFile(currentImage);
            imageDataDb.delete(currentImage);
            descriptionHistory = "Mengubah bukti muat";
            orderItemHistories.add(
                    this.addOrderItemHistory(orderItem, descriptionHistory, sopir)
            );
        } else {
            orderItem.setStatusOrder(2);
            descriptionHistory = "Mengunggah bukti muat";
            orderItemHistories.add(
                    this.addOrderItemHistory(orderItem, descriptionHistory, sopir)
            );
        }

        muatImage.setOrderItem(orderItem);
        imageDataDb.save(muatImage);
        return orderItem;
    }

    @Override
    public OrderItem uploadImageBongkar(String idOrderItem, MultipartFile imageFile) throws IOException {
        OrderItem orderItem = this.findOrderItemById(idOrderItem);
        ImageData imageData = storageService.uploadImageAndSaveToDB(
                imageFile,
                imageFile.getOriginalFilename() + "_bongkar_" + idOrderItem
        );
        BongkarMuatImage bongkarImage = modelMapper.map(imageData, BongkarMuatImage.class);

        ImageData currentImage;
        currentImage = orderItem.getBuktiBongkar();
        orderItem.setBuktiBongkar(bongkarImage);
        this.saveImageBongkarMuat(orderItem);

        // order item history
        var orderItemHistories = orderItem.getOrderItemHistories();
        String sopir = "Sopir " + orderItem.getSopir().getName();
        String descriptionHistory;

        if (currentImage != null) {
            storageService.deleteImageFile(currentImage);
            imageDataDb.delete(currentImage);
            descriptionHistory = "Mengubah bukti bongkar";
            orderItemHistories.add(
                    this.addOrderItemHistory(orderItem, descriptionHistory, sopir)
            );
        } else {
            orderItem.setStatusOrder(4);
            descriptionHistory = "Mengunggah bukti bongkar";
            orderItemHistories.add(
                    this.addOrderItemHistory(orderItem, descriptionHistory, sopir)
            );
        }
        bongkarImage.setOrderItem(orderItem);
        imageDataDb.save(bongkarImage);
        return orderItem;
    }

    @Override
    public ImageData getImageMuat(String idOrderItem) {
        OrderItem orderItem = this.findOrderItemById(idOrderItem);
        ImageData imageData = orderItem.getBuktiMuat();
        return imageData;
    }

    @Override
    public ImageData getImageBongkar(String idOrderItem) {
        OrderItem orderItem = this.findOrderItemById(idOrderItem);
        ImageData imageData = orderItem.getBuktiBongkar();
        return imageData;
    }

    @Override
    public void deleteImageMuat(String idOrderItem) {
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
    public void deleteImageBongkar(String idOrderItem) {
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
    public OrderItem getOrderItemById(String idOrderItem) {
        OrderItem orderItem = orderItemDb.findById(idOrderItem).orElse(null);
        if (orderItem == null) {
            throw new NoSuchElementException("Id order tidak valid");
        }
        return orderItem;
    }

    @Override
    public List<OrderItem> getAllOrderItemByIdOrder(String idOrder){
        return orderItemDb.findByIdOrder(idOrder);
    }

    @Override
    public OrderItemHistory addOrderItemHistory(OrderItem orderItem, String description,
                                                String createdBy) {
        var orderItemHistory = new OrderItemHistory();
        orderItemHistory.setOrderItem(orderItem);
        orderItemHistory.setDescription(description);
        orderItemHistory.setCreatedBy(createdBy);
        return orderItemHistoryDb.save(orderItemHistory);
    }

    @Override
    public Order getOrderById(String orderId) {
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

    @Override
    public Order getOrderByOrderItem(String idOrderItem) {
        return orderDb.findByOrderItemId(idOrderItem).orElseThrow(
                () -> new NoSuchElementException("Order tidak ditemukan")
        );
    }

    public BigDecimal getTotalExpenditureByKlienInRange(UUID klienId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Order> orders = orderDb.findByKlienIdAndCreatedAtBetween(klienId, startDateTime, endDateTime);
        return calculateTotalExpenditure(orders);
    }

    @Override
    public BigDecimal getTotalExpenditureByKlienDaily(UUID klienId, LocalDate date) {
        LocalDateTime startDateTime = date.atStartOfDay();
        LocalDateTime endDateTime = date.atTime(23, 59, 59);
        return getTotalExpenditureByKlienInRange(klienId, startDateTime, endDateTime);
    }

    @Override
    public BigDecimal getTotalExpenditureByKlienMonthly(UUID klienId, YearMonth yearMonth) {
        LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        return getTotalExpenditureByKlienInRange(klienId, startDateTime, endDateTime);
    }

    @Override
    public BigDecimal getTotalExpenditureByKlienYearly(UUID klienId, Year year) {
        LocalDateTime startDateTime = year.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = year.atMonth(12).atEndOfMonth().atTime(23, 59, 59);
        return getTotalExpenditureByKlienInRange(klienId, startDateTime, endDateTime);
    }

    @Override
    public BigDecimal calculateTotalExpenditure(List<Order> orders) {
        BigDecimal totalExpenditure = BigDecimal.ZERO;
        for (Order order : orders) {
            BigDecimal orderTotalPrice = BigDecimal.valueOf(order.getTotalPrice());
            totalExpenditure = totalExpenditure.add(orderTotalPrice);
        }
        return totalExpenditure;
    }

    @Override
    public List<OrderItem> getAllOrderItemDiprosesByKlienId(UUID klienId) {
        return orderItemDb.findByKlienIdAndStatusNotIn(klienId);
    }

    @Override
    public int countCompletedOrderItemsByKlienId(UUID klienId) {
        return orderItemDb.countCompletedOrderItemsByKlienId(klienId);
    }
}
