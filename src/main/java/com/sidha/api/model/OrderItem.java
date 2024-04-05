package com.sidha.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sidha.api.model.enumerator.TipeBarang;
import com.sidha.api.model.enumerator.TipeTruk;

import java.util.List;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_item")
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id = UUID.randomUUID();

  @Column(name = "tanggal_pengiriman")
  private Date tanggalPengiriman;

  @Column(name = "is_pecah_belah")
  private boolean isPecahBelah;

  @Column(name = "tipe_barang")
  private TipeBarang tipeBarang;

  @Column(name = "tipe_truk")
  private TipeTruk tipeTruk;

  @Column(name = "kota_asal")
  private String kotaAsal;

  @Column(name = "alamat_asal")
  private String alamatAsal;

  @Column(name = "kota_tujuan")
  private String kotaTujuan;

  @Column(name = "alamat_tujuan")
  private String alamatTujuan;

  @ElementCollection
  @Column(name = "multidrop")
  private List<String> multidrop;

  @Column(name = "keterangan")
  private String keterangan;

  @ManyToOne
  @JoinColumn(name = "order_id")
@JsonBackReference
  private Order order;
}
