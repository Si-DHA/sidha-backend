package com.sidha.api.DTO.request.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderItemRequestDTO {

  private String status;

  private boolean isPecahBelah;

  private String tipeBarang;

  private String tipeTruk;

  private String source;

  private String alamatPenjemputan;

  private String destination;

  private String alamatPengiriman;

  private List<String> multidrop;

  private String keterangan;

}
