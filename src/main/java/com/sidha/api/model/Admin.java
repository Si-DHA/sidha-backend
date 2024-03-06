package com.sidha.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue(value = "admin")
public class Admin extends UserModel{
  @Column(name = "is_super_admin")
  private boolean isSuperAdmin = false; 

}
