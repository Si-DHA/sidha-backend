package com.sidha.api.model.order;

import com.sidha.api.model.Invoice;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sidha.api.model.user.Klien;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
  @Id
  private String id;

  @Column(name = "created_at", nullable = false, updatable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "updated_at", nullable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime updatedAt = LocalDateTime.now();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @JsonManagedReference
  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  private List<OrderItem> orderItems;

  @Column(name = "total_price")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long totalPrice;

  @Column(name = "tanggal_pengiriman")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private Date tanggalPengiriman;

  @ManyToOne
  @JoinColumn(name = "klien_id")
  @JsonBackReference
  private Klien klien;

  @OneToOne(cascade = CascadeType.ALL)
  @JsonManagedReference
  private Invoice invoice;
}
