package com.sidha.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contract")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "contract_id")
    private UUID contractId = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Klien client;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "client_company", nullable = false)
    private String clientCompany;

    @Column(name = "company_address", nullable = false)
    private String companyAddress;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Lob
    @Column(name = "document", nullable = true)
    private byte[] document;

    @Lob
    @Column(name = "signature", nullable = true)
    private byte[] signature;
}
