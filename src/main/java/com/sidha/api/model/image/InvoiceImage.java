package com.sidha.api.model.image;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sidha.api.model.Invoice;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue(value = "invoice")
public class InvoiceImage extends ImageData {

    @ManyToOne
    @JsonBackReference
    private Invoice invoice;
    private String alasanPenolakan;

    // 0: belum dikonfirmasi
    // 1: dikonfirmasi
    // -1: ditolak
    private int status;
}
