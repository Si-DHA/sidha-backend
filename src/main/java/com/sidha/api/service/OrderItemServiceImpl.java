package com.sidha.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sidha.api.DTO.request.CreateOrderItemRequestDTO;
import com.sidha.api.model.PenawaranHarga;
import com.sidha.api.model.PenawaranHargaItem;
import com.sidha.api.model.enumerator.TipeBarang;
import com.sidha.api.model.enumerator.TipeTruk;
import com.sidha.api.model.order.Order;
import com.sidha.api.model.order.OrderItem;
import com.sidha.api.model.user.Klien;
import com.sidha.api.repository.OrderItemDb;

@Service
public class OrderItemServiceImpl implements OrderItemService {

  @Autowired
  private OrderItemDb orderItemRepository;

  @Override
  public OrderItem save(OrderItem orderItem) {
    return orderItemRepository.save(orderItem);
  }

  @Override
  public OrderItem saveOrderItem(CreateOrderItemRequestDTO request, Order order, Klien klien) {
    var orderItem = mappOrderItem(request, order, klien);
    return orderItemRepository.save(orderItem);
  }

  private OrderItem mappOrderItem(CreateOrderItemRequestDTO request, Order order, Klien klien) {
    var orderItem = new OrderItem();
    orderItem.setPecahBelah(request.isPecahBelah());
    orderItem.setTipeBarang(TipeBarang.valueOf(request.getTipeBarang()));
    orderItem.setTipeTruk(TipeTruk.valueOf(request.getTipeTruk()));
    orderItem.setSource(request.getSource());
    orderItem.setAlamatPenjemputan(request.getAlamatPenjemputan());
    orderItem.setDestination(request.getDestination());
    orderItem.setAlamatPengiriman(request.getAlamatPengiriman());
    orderItem.setMultidrop(request.getMultidrop());
    orderItem.setKeterangan(request.getKeterangan());
    orderItem.setPrice(getPrice(orderItem.getTipeTruk(), klien.getPenawaranHarga(), orderItem.getSource(), orderItem.getDestination()));

    orderItem.setOrder(order);
    return orderItem;
  }

  private int getPrice(TipeTruk tipeTruk, PenawaranHarga penawaranHarga, String source, String destination) {
    PenawaranHargaItem penawaranHargaItem = null;

    for (PenawaranHargaItem item : penawaranHarga.getListPenawaranHargaItem()) {
      if (item.getSource().equals(source) && item.getDestination().equals(destination)) penawaranHargaItem = item;
    }

    if (penawaranHargaItem == null) throw new RuntimeException("Penawaran Harga Item tidak ditemukan");

    switch (tipeTruk.getName()) {
      case "CDD":
        return penawaranHargaItem.getCddPrice();
      case "CCL":
        return penawaranHargaItem.getCddLongPrice();
      case "Wingbox":
        return penawaranHargaItem.getWingboxPrice();
      case "Fuso":
        return penawaranHargaItem.getFusoPrice();
      default:
        throw new RuntimeException("Tipe Truk tidak ditemukan");
    }
  }

}
