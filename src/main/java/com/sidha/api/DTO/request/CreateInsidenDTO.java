package com.sidha.api.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
public class CreateInsidenDTO {
    @NotBlank(message = "Kategori tidak boleh kosong")
    private String kategori;

    @NotBlank(message = "Lokasi tidak boleh kosong")
    private String lokasi;

    @NotBlank(message = "Keterangan tidak boleh kosong")
    private String keterangan;

    private MultipartFile buktiFoto;

    @NotNull(message = "ID Sopir tidak boleh kosong")
    private UUID sopirId;
}
