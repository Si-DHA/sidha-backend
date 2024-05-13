package com.sidha.api.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.sidha.api.DTO.request.order.CreateOrderRequestDTO;
import com.sidha.api.DTO.request.order.OrderConfirmRequestDTO;
import com.sidha.api.DTO.request.order.UpdateOrderRequestDTO;
import com.sidha.api.model.image.ImageData;
import com.sidha.api.model.order.Order;
import com.sidha.api.model.order.OrderItem;
import com.sidha.api.model.order.OrderItemHistory;

public interface OrderService {

    Order createOrder(CreateOrderRequestDTO request);

    List<Order> getOrdersByKlienId(UUID klienId);

    Order updateOrder(UpdateOrderRequestDTO request);

    Order confirmOrder(OrderConfirmRequestDTO request);

    List<Order> getAllOrders();

    OrderItem findOrderItemById(String idOrderItem);

    OrderItem uploadImageMuat(String idOrderItem, MultipartFile imageFile) throws IOException;

    OrderItem uploadImageBongkar(String idOrderItem, MultipartFile imageFile) throws IOException;

    ImageData getImageMuat(String idOrderItem);

    ImageData getImageBongkar(String idOrderItem);

    void deleteImageMuat(String idOrderItem);

    void deleteImageBongkar(String idOrderItem);

    OrderItem saveImageBongkarMuat(OrderItem orderItem);

    List<OrderItem> getAllOrderItemByIdSopir(UUID sopir);

    OrderItem getOrderItemById(String idOrderItem);

    List<OrderItem> getAllOrderItemByIdOrder(String idOrder);

    OrderItemHistory addOrderItemHistory(OrderItem orderItem, String description,
                                         String createdBy);

    Order getOrderById(String orderId);

    Order getPrice(CreateOrderRequestDTO request);

    List<String> getAllPossibleRute(UUID userId);

    Order getOrderByOrderItem(String idOrderItem);

    BigDecimal getTotalExpenditureByKlienInRange(UUID klienId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    BigDecimal getTotalExpenditureByKlienDaily(UUID klienId, LocalDate date);

    BigDecimal getTotalExpenditureByKlienMonthly(UUID klienId, YearMonth yearMonth);

    BigDecimal getTotalExpenditureByKlienYearly(UUID klienId, Year year);

    BigDecimal calculateTotalExpenditure(List<Order> orders);

    List<OrderItem> getAllOrderItemDiprosesByKlienId(UUID klienId);

    int countCompletedOrderItemsByKlienId(UUID klienId);

}
