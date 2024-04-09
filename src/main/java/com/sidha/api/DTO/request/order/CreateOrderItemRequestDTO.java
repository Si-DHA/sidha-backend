package com.sidha.api.DTO.request.order;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderItemRequestDTO {

  private boolean isPecahBelah;

  private String tipeBarang;

  private String tipeTruk;

  private String keterangan;

  private List<CreateRuteRequestDTO> rute;

}
