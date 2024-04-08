package com.sidha.api.DTO.request.order;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemConfirmRequestDTO {
    private UUID orderItemId;
    private boolean isAccepted;
    private String rejectionReason;
}
