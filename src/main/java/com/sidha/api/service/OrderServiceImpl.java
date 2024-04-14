package com.sidha.api.service;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

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

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private OrderDb orderDb;
    private OrderItemDb orderItemDb;
    private RuteDb ruteDb;
    private UserService userService;
    private InvoiceService invoiceService;
    private OrderItemHistoryDb orderItemHistoryDb;
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
        }
        return orderDb.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Override
    public List<Order> getAllOrders() {
        return orderDb.findAll();
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

}
