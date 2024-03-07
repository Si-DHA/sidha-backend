package com.sidha.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
  private List<PenawaranHargaItem> listPenawaranHargaItem;

  @OneToOne(mappedBy = "klien", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonManagedReference
  private PenawaranHarga penawaranHarga;
}
