package com.sidha.api.DTO.response;

import com.sidha.api.model.user.UserModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KontrakResponse {
  private UserModel user;
  private String kontrakUrl;
  private Date  updatedAt;
  private Date createdAt;
}
