package com.sidha.api.DTO.response;

import com.sidha.api.model.UserModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
  private String token;
  private UserModel user;
  private String imageUrl;
}
