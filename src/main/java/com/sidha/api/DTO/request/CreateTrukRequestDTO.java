package com.sidha.api.DTO.request;

import com.sidha.api.model.enumerator.TipeTruk;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTrukRequestDTO {
    @NotNull(message = "Nomor plat tidak boleh kosong")
    private String licensePlate;

    @NotNull(message = "Merk truk tidak boleh kosong")
    private String merk;

    @NotNull(message = "Tipe truk tidak boleh kosong")
    private TipeTruk type;

    @NotNull(message = "Nama pada STNK tidak boleh kosong")
    private String stnkName;

    @NotNull(message = "Tanggal expired KIR tidak boleh kosong")
    private Date expiredKir;

    @NotNull(message = "Panjang box tidak boleh kosong")
    @Positive(message = "Panjang box harus bernilai positif")
    private Double panjangBox;

    @NotNull(message = "Lebar box tidak boleh kosong")
    @Positive(message = "Lebar box harus bernilai positif")
    private Double lebarBox;

    @NotNull(message = "Tinggi box tidak boleh kosong")
    @Positive(message = "Tinggi box harus bernilai positif")
    private Double tinggiBox;

    @NotNull(message = "Kubikasi box tidak boleh kosong")
    @Positive(message = "Kubikasi box harus bernilai positif")
    private Double kubikasiBox;

    private UUID idSopir;
}
