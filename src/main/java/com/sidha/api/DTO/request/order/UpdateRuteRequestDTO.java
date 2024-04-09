package com.sidha.api.DTO.request.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRuteRequestDTO {

    private Long ruteId;

    private String source;
    
    private String destination;
    
    private String alamatPengiriman;
    
    private String alamatPenjemputan;
    
    private double price;
}
