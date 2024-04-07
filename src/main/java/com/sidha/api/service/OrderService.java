package com.sidha.api.service;

import com.sidha.api.DTO.request.CreateOrderRequestDTO;
import com.sidha.api.model.order.Order;

import java.util.List;

public interface OrderService {
  Order save(Order order);

  Order saveOrder(CreateOrderRequestDTO request);

  Order updateOrder(CreateOrderRequestDTO request);

  Order setStatusOrder(String idOrder, String status);

  Order getOrderById(String idOrder);

  Order deleteOrder(String idOrder);

  List<Order> getAllOrder();

}
