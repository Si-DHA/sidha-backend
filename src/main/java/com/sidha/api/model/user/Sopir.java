package com.sidha.api.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sidha.api.model.Truk;
import com.sidha.api.model.order.OrderItem;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue(value = "sopir")
public class Sopir extends UserModel {
  @Column(name = "is_available")
  private boolean isAvailable = false;

  @OneToOne(mappedBy = "sopir", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonBackReference
  private Truk truk;

}
