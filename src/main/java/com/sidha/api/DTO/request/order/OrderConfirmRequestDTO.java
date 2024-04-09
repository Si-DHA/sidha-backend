package com.sidha.api.DTO.request.order;

import java.util.UUID;
import java.util.List;

import lombok.Data;

@Data
public class OrderConfirmRequestDTO {
    private UUID orderId;
    private List<OrderItemConfirmRequestDTO> orderItems;
    private UUID karyawanId;
}
