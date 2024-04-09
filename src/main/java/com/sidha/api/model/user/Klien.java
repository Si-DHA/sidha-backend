package com.sidha.api.model.user;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sidha.api.model.PenawaranHarga;
import com.sidha.api.model.PenawaranHargaItem;
import com.sidha.api.model.order.Order;

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
@DiscriminatorValue(value = "klien")
public class Klien extends UserModel {
  @Column(name = "company_name")
  private String companyName;

  @OneToMany(mappedBy = "klien", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<PenawaranHargaItem> listPenawaranHargaItem = new ArrayList<>();

  @OneToOne(mappedBy = "klien", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonManagedReference
  private PenawaranHarga penawaranHarga;

  @OneToMany(mappedBy = "klien", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  private List<Order> orders = new ArrayList<>();
}
