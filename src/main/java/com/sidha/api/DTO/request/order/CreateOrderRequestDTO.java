package com.sidha.api.DTO.request.order;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequestDTO {
  private List<OrderItemRequestDTO> orderItems = new ArrayList<>();
  private Date tanggalPengiriman;
  private UUID klienId;
}
