package com.sidha.api.DTO.request;

import com.sidha.api.model.Klien;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePenawaranHargaRequestDTO {
    @NotNull(message = "Klien tidak boleh kosong")
    private UUID idKlien;

    @NotNull(message = "List PenawaranHargaItem tidak boleh kosong")
    private List<CreatePenawaranHargaItemRequestDTO> listPenawaranHargaItem;

    private LocalDateTime penawaranHargaCreatedAt;

    private LocalDateTime penawaranHargaUpdatedAt;
}
