package com.sidha.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "penawaran_item")
public class PenawaranItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "quotation_item_id")
    private UUID quotationItemId = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Klien client;

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = "penawaran_id", nullable = false)
    private Penawaran penawaran;

    @Column(name = "item_no", nullable = false)
    private Integer itemNo;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "cdd_price", nullable = false)
    private Long cddPrice;

    @Column(name = "cdd_long_price", nullable = false)
    private Long cddLongPrice;

    @Column(name = "wingbox_price", nullable = false)
    private Long wingboxPrice;

    @Column(name = "fuso_price", nullable = false)
    private Long fusoPrice;
}
