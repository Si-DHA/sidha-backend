package com.sidha.api.model.order;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "orderItem")
@Entity
@Table(name = "rute")
public class Rute {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "source")
    private String source;

    @Column(name = "destination")
    private String destination;

    @Column(name = "alamat_pengiriman")
    private String alamatPengiriman;

    @Column(name = "alamat_penjemputan")
    private String alamatPenjemputan;

    @Column(name = "price")
    private Integer price;

    @ManyToOne
    @JoinColumn(name = "order_item_id")
    @JsonBackReference
    private OrderItem orderItem;
}
