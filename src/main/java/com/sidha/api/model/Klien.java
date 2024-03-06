package com.sidha.api.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
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
  private List<PenawaranHargaItem> listPenawaranHargaItem;

  @OneToMany(mappedBy = "klien", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<PenawaranHarga> listPenawaranHarga;

}
