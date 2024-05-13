package com.sidha.api.model.image;

import com.sidha.api.model.order.OrderItem;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue(value = "order_item")
public class BongkarMuatImage extends ImageData {
    @ManyToOne
    @JsonBackReference
    private OrderItem orderItem;
}
