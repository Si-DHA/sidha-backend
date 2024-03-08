package com.sidha.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sidha.api.model.enumerator.TipeTruk;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE truk SET is_deleted = true WHERE id_truk=?")
@SQLRestriction(value = "is_deleted = false")
@Table(name = "truk")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Truk {
    @Id
    private UUID idTruk = UUID.randomUUID();

    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;

    @Column(name = "merk", nullable = false)
    private String merk;

    @Enumerated(EnumType.STRING)
    private TipeTruk type;

    @Column(name = "stnk_name", nullable = false)
    private String stnkName;

    @Column(name = "expired_kir", nullable = false)
    private Date expiredKir;

    @Column(name = "panjang_box", nullable = false)
    private Double panjangBox;

    @Column(name = "lebar_box", nullable = false)
    private Double lebarBox;

    @Column(name = "tinggi_box", nullable = false)
    private Double tinggiBox;

    @Column(name = "kubikasi_box", nullable = false)
    private Double kubikasiBox;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "truk")
    @JsonManagedReference
    private Sopir sopir;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}
