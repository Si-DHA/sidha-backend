package com.sidha.api.model;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue(value = "penawaran_harga")
public class PenawaranHarga{
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_klien", referencedColumnName = "id")
    private Klien klien;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idPenawaranHarga = UUID.randomUUID();

    @OneToMany(mappedBy = "penawaranHarga", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PenawaranHargaItem> listPenawaranHargaItem = new ArrayList<>();

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date penawaranHargaCreatedAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date penawaranHargaUpdatedAt;
}
