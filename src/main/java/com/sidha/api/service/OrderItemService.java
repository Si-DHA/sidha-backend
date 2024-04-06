package com.sidha.api.service;

import com.sidha.api.DTO.request.CreateOrderItemRequestDTO;
import com.sidha.api.model.Order;
import com.sidha.api.model.OrderItem;

public interface OrderItemService {
  OrderItem save(OrderItem orderItem);

  OrderItem saveOrderItem(CreateOrderItemRequestDTO request, Order order);
  
}
