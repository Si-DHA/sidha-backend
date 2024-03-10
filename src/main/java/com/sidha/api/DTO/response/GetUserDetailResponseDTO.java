package com.sidha.api.DTO.response;

import com.sidha.api.model.enumerator.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUserDetailResponseDTO {
  private String name;
  private String password;
  private String email;
  private String username;
  private Role role;
  private String address;
  private String phone;
  private boolean isSuperAdmin;
  private String position;
  private String companyName;
  private boolean isAvailable;
  private String imageUrl;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Boolean isDeleted;

}
