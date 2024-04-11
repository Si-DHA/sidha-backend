package com.sidha.api.service;

import java.util.UUID;
import java.util.List;

import com.sidha.api.DTO.request.order.CreateOrderRequestDTO;
import com.sidha.api.DTO.request.order.OrderConfirmRequestDTO;
import com.sidha.api.DTO.request.order.UpdateOrderRequestDTO;
import com.sidha.api.model.image.ImageData;
import com.sidha.api.model.order.Order;
import com.sidha.api.model.order.OrderItem;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface OrderService {

    Order createOrder(CreateOrderRequestDTO request);

    List<Order> getOrdersByKlienId(UUID klienId);

    Order updateOrder(UpdateOrderRequestDTO request);

    Order confirmOrder(OrderConfirmRequestDTO request);

    List<Order> getAllOrders();

    OrderItem findOrderItemById(UUID idOrderItem);

    OrderItem uploadImageBongkarMuat(UUID idOrderItem, boolean isBongkar, MultipartFile imageFile) throws IOException;

    ImageData getImageBongkarMuat(UUID idOrderItem, boolean isBongkar);

    void deleteImageBongkarMuat(UUID idInvoice, boolean isBongkar);

    OrderItem saveImageBongkarMuat(OrderItem orderItem);
}
