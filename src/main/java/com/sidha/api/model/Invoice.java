package com.sidha.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invoice")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Invoice {
    @Id
    private final UUID idInvoice = UUID.randomUUID();

    // TODO: one-to-one Order

//    @JsonIgnore
//    private ImageData buktiDP;

    // 0: belum dikonfirmasi
    // 1: dikonfirmasi
    // 2: ditolak
    @Column(name = "status_dp", nullable = false)
    private int statusDP = 0;

    // alasan penolakan
    @Column(name = "alasan_dp")
    private String alasanDP;

//    @JsonIgnore
//    private ImageData buktiPelunasan;

    @Column(name = "status_pelunasan", nullable = false)
    private int statusPelunasan = 0;

    @Column(name = "alasan_pelunasan")
    private String alasanPelunasan;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;
}
