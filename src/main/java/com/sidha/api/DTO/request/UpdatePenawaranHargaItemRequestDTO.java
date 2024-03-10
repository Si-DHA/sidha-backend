package com.sidha.api.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePenawaranHargaItemRequestDTO {
    @NotNull
    private UUID idPenawaranHargaItem;
    
    private Integer cddPrice;

    private Integer cddLongPrice;

    private Integer wingboxPrice;

    private Integer fusoPrice;
}