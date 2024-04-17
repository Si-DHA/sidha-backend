package com.sidha.api.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sidha.api.model.order.OrderItem;
import com.sidha.api.model.user.Sopir;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tawaran_kerja")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TawaranKerja {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_item_id", referencedColumnName = "id")
    private OrderItem orderItem;

    @ManyToOne
    @JoinColumn(name = "sopir_id", referencedColumnName = "id")
    @JsonManagedReference
    private Sopir sopir;

    @Column(name = "lokasi")
    private String lokasi;

    @Column(name = "is_dikonfirmasi_karyawan")
    private Boolean isDikonfirmasiKaryawan;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

}
