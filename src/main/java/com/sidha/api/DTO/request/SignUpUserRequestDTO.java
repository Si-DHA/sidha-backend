package com.sidha.api.DTO.request;

import com.sidha.api.model.*;
import com.sidha.api.model.enumerator.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpUserRequestDTO<T extends UserModel> {
  private String name;
  private String password;
  private String email;
  private String username;
  private Role role;
  private String address;
  private String phone;
  private boolean isSuperAdmin = false;
  private String position;
  private String companyName;
  private boolean isAvailable = false;
}
