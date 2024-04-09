package com.sidha.api.model.user;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sidha.api.model.Insiden;
import com.sidha.api.model.Truk;
import com.sidha.api.model.UserModel;
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

  @OneToMany(mappedBy = "sopir", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  private List<Insiden> insidens;

}
