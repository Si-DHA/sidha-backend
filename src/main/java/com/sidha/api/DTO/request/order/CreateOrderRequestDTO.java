package com.sidha.api.DTO.request.order;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequestDTO {
  
  private List<CreateOrderItemRequestDTO> orderItems;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
  private Date tanggalPengiriman;

  private UUID klienId;
}
