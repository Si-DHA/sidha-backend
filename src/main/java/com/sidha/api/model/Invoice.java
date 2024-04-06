package com.sidha.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sidha.api.model.image.InvoiceImage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invoice")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Invoice {
    @Id
    private final UUID idInvoice = UUID.randomUUID();

    // TODO: one-to-one Order

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL)
    private InvoiceImage buktiDp;

    @Column(name = "total_dp")
    private BigDecimal totalDp;

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL)
    private InvoiceImage buktiPelunasan;

    @Column(name = "total_pelunasan")
    private BigDecimal totalPelunasan;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;
}
