package com.sidha.api.service;

import java.util.UUID;

import com.sidha.api.DTO.request.order.CreateOrderRequestDTO;
import com.sidha.api.model.order.Order;

public interface OrderService {

    Order createOrder(CreateOrderRequestDTO request, UUID userId);
}
