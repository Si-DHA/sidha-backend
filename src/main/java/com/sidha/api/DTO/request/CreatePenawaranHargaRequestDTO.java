package com.sidha.api.DTO.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePenawaranHargaRequestDTO {
    @NotNull(message = "Klien tidak boleh kosong")
    private UUID idKlien;

    private List<CreatePenawaranHargaItemRequestDTO> listPenawaranHargaItem;
}
