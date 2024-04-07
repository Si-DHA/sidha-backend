package com.sidha.api.service;

import com.sidha.api.DTO.request.CreateOrderItemRequestDTO;
import com.sidha.api.model.order.Order;
import com.sidha.api.model.order.OrderItem;
import com.sidha.api.model.user.Klien;

public interface OrderItemService {
  OrderItem save(OrderItem orderItem);

  OrderItem saveOrderItem(CreateOrderItemRequestDTO request, Order order, Klien klien);
  
}
