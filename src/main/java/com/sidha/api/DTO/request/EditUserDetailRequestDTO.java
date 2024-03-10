package com.sidha.api.DTO.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditUserDetailRequestDTO {
  private String name;
  private String address;
  private String phone;
  private boolean isSuperAdmin;
  private String position;
  private MultipartFile imageFile;
}
