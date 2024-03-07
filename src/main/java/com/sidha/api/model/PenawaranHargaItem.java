package com.sidha.api.model;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
@SQLDelete(sql = "UPDATE user_table SET is_deleted = true WHERE id=?")
@SQLRestriction(value = "is_deleted = false")
@DiscriminatorValue(value = "penawaran_harga_item")
public class PenawaranHargaItem {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_klien", referencedColumnName = "id")
    @JsonBackReference
    private Klien klien;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_penawaran_harga", referencedColumnName = "id_penawaran_harga")
    @JsonBackReference
    private PenawaranHarga penawaranHarga;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idPenawaranHargaItem = UUID.randomUUID();

    @Column(name = "source")
    private String source;

    @Column(name = "destination")
    private String destination;

    @Column(name = "cdd_price")
    private Integer cddPrice;
    
    @Column(name = "cdd_long_price")
    private Integer cddLongPrice;

    @Column(name = "wingbox_price")
    private Integer wingboxPrice;

    @Column(name = "fuso_price")
    private Integer fusoPrice;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}