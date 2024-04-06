package com.sidha.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Insiden {
    @Id
    @GeneratedValue
    private UUID id;

    private String kategori;
    private String lokasi;
    private String keterangan;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_data_id", referencedColumnName = "id")
    private ImageData buktiFoto;

    @ManyToOne
    @JoinColumn(name="sopir_id", nullable=false)
    private Sopir sopir;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt = LocalDateTime.now();
}
