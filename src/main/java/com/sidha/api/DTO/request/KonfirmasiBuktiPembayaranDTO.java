package com.sidha.api.DTO.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KonfirmasiBuktiPembayaranDTO {
    @NotNull(message = "ID invoice tidak boleh kosong")
    private UUID idInvoice;
    @NotNull(message = "Jenis bukti pembayaran tidak boleh kosong")
    private Boolean isPelunasan;

    @NotNull(message = "Status konfirmasi tidak boleh kosong")
    private Boolean isConfirmed;

    private String alasanPenolakan;
}
