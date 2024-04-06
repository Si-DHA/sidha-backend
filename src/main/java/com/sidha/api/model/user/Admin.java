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
@DiscriminatorValue(value = "admin")
public class Admin extends UserModel {
  @Column(name = "is_super_admin")
  private boolean isSuperAdmin = false;
}