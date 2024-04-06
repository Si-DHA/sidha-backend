package com.sidha.api.model.user;

import com.sidha.api.model.UserModel;
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
@DiscriminatorValue(value = "karyawan")
public class Karyawan extends UserModel {
  @Column(name = "position")
  private String position;

}
