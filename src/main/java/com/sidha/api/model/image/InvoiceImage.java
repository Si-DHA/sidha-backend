package com.sidha.api.model.image;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sidha.api.model.Invoice;
import com.sidha.api.model.image.ImageData;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
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

    @OneToOne
    @JsonBackReference
    private Invoice invoice;
    private String alasanPenolakan;

    // 0: belum dikonfirmasi
    // 1: dikonfirmasi
    // 2: ditolak
    private int status = 0;
}
