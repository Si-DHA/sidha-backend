package com.sidha.api.model.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sidha.api.model.TawaranKerja;
import com.sidha.api.model.enumerator.TipeBarang;
import com.sidha.api.model.enumerator.TipeTruk;
import com.sidha.api.model.image.BongkarMuatImage;
import com.sidha.api.model.user.Sopir;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "order")
@Entity
@Table(name = "order_item")
public class OrderItem { // 1 order item = 1 truk

  @Id
  private String id;

  @Column(name = "price")
  private Long price;

  @Column(name = "status_order")
  private int statusOrder;

  @Column(name = "alasan_penolakan")
  private String alasanPenolakan;

  @Column(name = "is_pecah_belah")
  private Boolean isPecahBelah;

  @Enumerated(EnumType.STRING)
  private TipeBarang tipeBarang;

  @Enumerated(EnumType.STRING)
  private TipeTruk tipeTruk;

  @Column(name = "keterangan")
  private String keterangan;
  // start of dashboard perusahaan
  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_at", updatable = false, nullable = false)
  private Date createdAt;

  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_at", nullable = false)
  private Date updatedAt;
  // end of dashboard perusahaan

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  @JsonBackReference
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_sopir", referencedColumnName = "id")
  @JsonBackReference
  private Sopir sopir;

  @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonManagedReference
  @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
  private List<Rute> rute;

  @JsonManagedReference
  @OneToOne(cascade = CascadeType.ALL)
  private BongkarMuatImage buktiBongkar;

  @JsonManagedReference
  @OneToOne(cascade = CascadeType.ALL)
  private BongkarMuatImage buktiMuat;

  @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonManagedReference
  @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
  private List<OrderItemHistory> orderItemHistories;

  @ManyToOne
  @JoinColumn(name = "tawaran_kerja")
  @JsonBackReference
  private TawaranKerja tawaranKerja;
}
