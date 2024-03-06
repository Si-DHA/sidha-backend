package com.sidha.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue(value = "sopir")
public class Sopir extends UserModel{
  @Column(name = "is_available")
  private boolean isAvailable = false;

  @OneToOne(mappedBy = "sopir", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonBackReference
  private Truk truk;
  
}
