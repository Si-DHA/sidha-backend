package com.sidha.api.service;

import java.util.UUID;
import java.util.List;

import com.sidha.api.DTO.request.order.CreateOrderRequestDTO;
import com.sidha.api.DTO.request.order.OrderConfirmRequestDTO;
import com.sidha.api.DTO.request.order.UpdateOrderRequestDTO;
import com.sidha.api.model.order.Order;

public interface OrderService {

    Order createOrder(CreateOrderRequestDTO request);

    List<Order> getOrdersByKlienId(UUID klienId);

    Order updateOrder(UpdateOrderRequestDTO request);

    Order confirmOrder(OrderConfirmRequestDTO request);

    List<Order> getAllOrders();
}
