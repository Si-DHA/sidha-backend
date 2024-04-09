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
import com.sidha.api.model.order.Rute;
import com.sidha.api.model.user.Klien;
import com.sidha.api.repository.OrderDb;
import com.sidha.api.repository.OrderItemDb;
import com.sidha.api.repository.RuteDb;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private OrderDb orderDb;
    private OrderItemDb orderItemDb;
    private RuteDb ruteDb;
    private UserService userService;

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
        return orderDb.save(order);
    }

    private OrderItem saveOrderItem(CreateOrderItemRequestDTO request, Order order, Klien klien) {
        var orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setPecahBelah(request.isPecahBelah());
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
        rute.setPrice(getPriceRute(orderItem.getTipeTruk(), request, klien.getListPenawaranHargaItem()));

        return ruteDb.save(rute);
    }

    private Integer getPriceRute(TipeTruk tipeTruk, CreateRuteRequestDTO r,
            List<PenawaranHargaItem> listPenawaranHargaItem) {
        for (PenawaranHargaItem penawaranHargaItem : listPenawaranHargaItem) {
            if (penawaranHargaItem.getDestination().equals(r.getDestination())
                    && penawaranHargaItem.getSource().equals(r.getSource())) {
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

        // request.getOrderItems().forEach(item -> {
        // var orderItem = orderItemDb.findById(item.getId())
        // .orElseThrow(() -> new IllegalArgumentException("Order Item not found"));
        // orderItem.setPecahBelah(item.isPecahBelah());
        // orderItem.setTipeBarang(TipeBarang.valueOf(item.getTipeBarang()));
        // orderItem.setTipeTruk(TipeTruk.valueOf(item.getTipeTruk()));
        // orderItemDb.save(orderItem);

        // var rute = new ArrayList<Rute>();
        // item.getRute().forEach(r -> {
        // var ruteSaved = saveRute(r, orderItem, order.getKlien());
        // rute.add(ruteSaved);
        // });
        // orderItem.setRute(rute);
        // orderItem.setPrice(rute.stream().mapToDouble(Rute::getPrice).sum());
        // orderItemDb.save(orderItem);
        // });
        return null;
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
            orderItemDb.save(orderItem);
        }
        return orderDb.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Override
    public List<Order> getAllOrders() {
        return orderDb.findAll();
    }

}
