package com.sidha.api.service;

import java.util.UUID;
import java.util.List;

import com.sidha.api.DTO.request.order.CreateOrderRequestDTO;
import com.sidha.api.DTO.request.order.OrderConfirmRequestDTO;
import com.sidha.api.DTO.request.order.UpdateOrderRequestDTO;
import com.sidha.api.model.image.ImageData;
import com.sidha.api.model.order.Order;
import com.sidha.api.model.order.OrderItem;
import com.sidha.api.model.order.OrderItemHistory;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.*;

public interface OrderService {

    Order createOrder(CreateOrderRequestDTO request);

    List<Order> getOrdersByKlienId(UUID klienId);

    Order updateOrder(UpdateOrderRequestDTO request);

    Order confirmOrder(OrderConfirmRequestDTO request);

    List<Order> getAllOrders();

    OrderItem findOrderItemById(UUID idOrderItem);

    OrderItem uploadImageMuat(UUID idOrderItem, MultipartFile imageFile) throws IOException;

    OrderItem uploadImageBongkar(UUID idOrderItem, MultipartFile imageFile) throws IOException;

    ImageData getImageMuat(UUID idOrderItem);

    ImageData getImageBongkar(UUID idOrderItem);

    void deleteImageMuat(UUID idOrderItem);

    void deleteImageBongkar(UUID idOrderItem);

    OrderItem saveImageBongkarMuat(OrderItem orderItem);

    List<OrderItem> getAllOrderItemByIdSopir(UUID sopir);

    OrderItem getOrderItemById(UUID idOrderItem);

    List<OrderItem> getAllOrderItemByIdOrder(UUID idOrder);

    OrderItemHistory addOrderItemHistory(OrderItem orderItem, String description,
                                         String createdBy);

    Order getOrderById(UUID orderId);

    Order getPrice(CreateOrderRequestDTO request);

    List<String> getAllPossibleRute(UUID userId);

    Order getOrderByOrderItem(UUID idOrderItem);

    BigDecimal getTotalExpenditureByKlienInRange(UUID klienId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    BigDecimal getTotalExpenditureByKlienDaily(UUID klienId, LocalDate date);

    BigDecimal getTotalExpenditureByKlienMonthly(UUID klienId, YearMonth yearMonth);

    BigDecimal getTotalExpenditureByKlienYearly(UUID klienId, Year year);

    BigDecimal calculateTotalExpenditure(List<Order> orders);

    List<OrderItem> getAllOrderItemDiprosesByKlienId(UUID klienId);

    int countCompletedOrderItemsByKlienId(UUID klienId);

}
