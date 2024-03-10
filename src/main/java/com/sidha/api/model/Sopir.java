package com.sidha.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
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
