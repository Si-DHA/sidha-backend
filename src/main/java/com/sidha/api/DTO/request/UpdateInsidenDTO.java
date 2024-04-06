package com.sidha.api.DTO.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateInsidenDTO {
    @NotBlank(message = "Kategori tidak boleh kosong")
    private String kategori;

    @NotBlank(message = "Lokasi tidak boleh kosong")
    private String lokasi;

    @NotBlank(message = "Keterangan tidak boleh kosong")
    private String keterangan;

    private MultipartFile buktiFoto;
}
