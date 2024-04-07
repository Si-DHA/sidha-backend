package com.sidha.api.DTO.request;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequestDTO {
  private List<CreateOrderItemRequestDTO> orderItems = new ArrayList<>();
  private UUID userId;
}
