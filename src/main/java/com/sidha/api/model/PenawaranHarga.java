package com.sidha.api.model;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

import com.sidha.api.model.user.Klien;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue(value = "penawaran_harga")
public class PenawaranHarga{
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_klien", referencedColumnName = "id")
    @JsonBackReference
    private Klien klien;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id_penawaran_harga")
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
