package com.sidha.api.model.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sidha.api.model.enumerator.TipeBarang;
import com.sidha.api.model.enumerator.TipeTruk;
import com.sidha.api.model.user.Sopir;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_item")
public class OrderItem { // 1 order item = 1 truk

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id = UUID.randomUUID();

  @Column(name = "price")
  private double price;

  @Column(name = "status_order")
  private int statusOrder;

  @Column(name = "alasan_penolakan")
  private String alasanPenolakan;

  @Column(name = "is_pecah_belah")
  private boolean isPecahBelah;

  @Column(name = "tipe_barang")
  private TipeBarang tipeBarang;

  @Column(name = "tipe_truk")
  private TipeTruk tipeTruk;

  @Column(name = "keterangan")
  private String keterangan;

  @ManyToOne
  @JoinColumn(name = "order_id")
  @JsonBackReference
  private Order order;

  @ManyToOne
  @JoinColumn(name = "sopir_id")
  @JsonBackReference
  private Sopir sopir;

  @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL)
  private List<Rute> rute;
}
