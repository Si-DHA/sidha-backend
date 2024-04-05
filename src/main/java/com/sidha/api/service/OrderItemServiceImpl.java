package com.sidha.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sidha.api.DTO.request.CreateOrderItemRequestDTO;
import com.sidha.api.model.Order;
import com.sidha.api.model.OrderItem;
import com.sidha.api.model.enumerator.TipeBarang;
import com.sidha.api.model.enumerator.TipeTruk;
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
  public OrderItem saveOrderItem(CreateOrderItemRequestDTO request, Order order) {
    var orderItem = new OrderItem();
    orderItem.setTanggalPengiriman(request.getTanggalPengiriman());
    orderItem.setPecahBelah(request.isPecahBelah());
    orderItem.setTipeBarang(TipeBarang.valueOf(request.getTipeBarang()));
    orderItem.setTipeTruk(TipeTruk.valueOf(request.getTipeTruk()));
    orderItem.setKotaAsal(request.getKotaAsal());
    orderItem.setAlamatAsal(request.getAlamatAsal());
    orderItem.setKotaTujuan(request.getKotaTujuan());
    orderItem.setAlamatTujuan(request.getAlamatTujuan());
    orderItem.setMultidrop(request.getMultidrop());
    orderItem.setKeterangan(request.getKeterangan());
    
    orderItem.setOrder(order);
    return orderItemRepository.save(orderItem);
  }

}
