package com.sidha.api.DTO.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcceptTawaranKerjaDTO {
    @NotNull(message = "Sopir tidak boleh kosong")
    private UUID sopirId;

    @NotNull(message = "Order item tidak boleh kosong")
    private UUID orderItemId;

    @NotNull(message = "Lokasi Sopir tidak boleh kosong")
    private String lokasi;
}
