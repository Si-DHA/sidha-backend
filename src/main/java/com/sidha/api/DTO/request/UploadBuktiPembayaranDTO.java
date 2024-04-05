package com.sidha.api.DTO.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadBuktiPembayaranDTO {
    @NotNull(message = "ID invoice tidak boleh kosong")
    private UUID idInvoice;
    @NotNull(message = "Jenis bukti pembayaran tidak boleh kosong")
    private boolean isPelunasan;

    @NotNull(message = "Foto bukti pembayaran tidak boleh kosong")
    private MultipartFile imageFile;
}
