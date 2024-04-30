package com.sidha.api.DTO.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmTawaranKerjaDTO {
    @NotNull(message = "Tawaran Kerja tidak boleh kosong")
    private UUID tawaranKerjaId;

    @NotNull(message = "Karyawan tidak boleh kosong")
    private UUID karyawanId;
}
