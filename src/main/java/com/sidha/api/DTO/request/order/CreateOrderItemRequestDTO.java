package com.sidha.api.DTO.request.order;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderItemRequestDTO {

  private Boolean isPecahBelah;

  private String tipeBarang;

  private String tipeTruk;

  private String keterangan;

  private List<CreateRuteRequestDTO> rute;

  public void setIsPecahBelah(Object isPecahBelah) {
    if (isPecahBelah instanceof Boolean) {
      this.isPecahBelah = (Boolean) isPecahBelah;
    } else if (isPecahBelah instanceof String) {
      this.isPecahBelah = Boolean.parseBoolean((String) isPecahBelah);
    }
  }

}
