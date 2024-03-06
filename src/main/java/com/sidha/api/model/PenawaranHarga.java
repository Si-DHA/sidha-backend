package com.sidha.api.model;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;
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
    private List<PenawaranHargaItem> listPenawaranHargaItem;

    @Column(name = "penawaran_harga_created_at", nullable = true)
    private LocalDateTime penawaranHargaCreatedAt;

    @Column(name = "penawaran_harga_updated_at", nullable = true)
    private LocalDateTime penawaranHargaUpdatedAt;
}
