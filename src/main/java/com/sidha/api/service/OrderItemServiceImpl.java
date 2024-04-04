package com.sidha.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sidha.api.DTO.request.CreateOrderItemRequestDTO;
import com.sidha.api.model.OrderItem;
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
  public OrderItem saveOrderItem(CreateOrderItemRequestDTO request) {
    var orderItem = new OrderItem();
    orderItem.setTanggalPengiriman(request.getTanggalPengiriman());
    orderItem.setPecahBelah(request.isPecahBelah());

    return orderItemRepository.save(orderItem);
  }

}
