package com.sidha.api.DTO.response;

import com.sidha.api.DTO.UserDTO;
import com.sidha.api.model.UserModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse<T extends UserModel> {
  private String token;
  private UserDTO<T> user;
}
