package com.sidha.api.DTO.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePenawaranHargaItemRequestDTO {
    @NotNull(message = "Klien tidak boleh kosong")
    private UUID idKlien;

    @NotNull(message = "ID penawaran harga tidak boleh kosong")
    private UUID idPenawaranHarga;

    @NotNull(message = "Source tidak boleh kosong")
    private String source;

    @NotNull(message = "Destination tidak boleh kosong")
    private String destination;

    private Integer cddPrice;

    private Integer cddLongPrice;

    private Integer wingboxPrice;

    private Integer fusoPrice;
}
