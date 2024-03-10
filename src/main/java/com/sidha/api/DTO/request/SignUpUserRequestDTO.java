package com.sidha.api.DTO.request;

import org.springframework.web.multipart.MultipartFile;

import com.sidha.api.model.enumerator.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpUserRequestDTO {
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
  private MultipartFile imageFile;

}
