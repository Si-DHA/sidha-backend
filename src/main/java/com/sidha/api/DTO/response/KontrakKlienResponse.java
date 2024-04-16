package com.sidha.api.DTO.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.UUID;

@Builder
@Data
@Getter
@Setter
public class KontrakKlienResponse {
  private UUID userId;
  private String nama;
  private String companyName;
  private String imageUrl;
  private String email;
  private String phone;
  private String kontrakUrl;
  private Date createdAt;
  private Date updatedAt;

}
