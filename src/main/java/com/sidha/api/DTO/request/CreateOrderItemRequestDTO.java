package com.sidha.api.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderItemRequestDTO {

  private Date tanggalPengiriman;

  private boolean isPecahBelah;

  private String tipeBarang;

  private String tipeTruk;

  private String kotaAsal;

  private String alamatAsal;

  private String kotaTujuan;

  private String alamatTujuan;

  private List<String> multidrop;

  private String keterangan;

}
