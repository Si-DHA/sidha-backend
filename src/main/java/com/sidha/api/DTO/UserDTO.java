package com.sidha.api.DTO;

import com.sidha.api.model.*;
import com.sidha.api.model.enumerator.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO<T extends UserModel> {
  private String name;
  private String password;
  private String email;
  private String username = this.email;
  private Role role;
  private String address;
  private String phone;
  private boolean isSuperAdmin = false;
  private String position;
  private String companyName;
  private boolean isAvailable = false;

  public UserDTO(T user) {
    this.name = user.getName();
    this.password = user.getPassword();
    this.email = user.getEmail();
    this.address = user.getAddress();
    this.phone = user.getPhone();
    this.username= user.getEmail();
    if (user instanceof Admin) {
      Admin admin = (Admin) user;
      this.isSuperAdmin = admin.getIsDeleted();
      this.role = Role.ADMIN;
    }
    if (user instanceof Karyawan) {
      Karyawan karyawan = (Karyawan) user;
      this.position = karyawan.getPosition();
      this.role = Role.KARYAWAN;
    }
    if (user instanceof Klien) {
      Klien klien = (Klien) user;
      this.companyName = klien.getCompanyName();
      this.role = Role.KLIEN;
    }

    if (user instanceof Sopir) {
      Sopir sopir = (Sopir) user;
      this.isAvailable = false;
      this.role = Role.SOPIR;
    }

  }

}
