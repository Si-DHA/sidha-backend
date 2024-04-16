package com.sidha.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sidha.api.model.user.Sopir;
import com.sidha.api.model.image.ImageData;
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
@Table(name = "insiden")
public class Insiden {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String kategori;
    private String lokasi;
    private String keterangan;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_data_id", referencedColumnName = "id")
    private ImageData buktiFoto;

    @ManyToOne
    @JoinColumn(name="sopir_id", nullable=false)
    @JsonBackReference
    private Sopir sopir;

    @Enumerated(EnumType.STRING)
    private InsidenStatus status = InsidenStatus.PENDING; // Default status

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public enum InsidenStatus {
        PENDING,
        ON_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}
