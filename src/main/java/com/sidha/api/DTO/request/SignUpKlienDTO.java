package com.sidha.api.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpKlienDTO {
  private String name;
  private String email;
  private String phone;
  private String address;
  private String password;
  private String companyName;
}
