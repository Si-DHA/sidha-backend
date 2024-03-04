package com.sidha.api.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPassUserRequestDTO {
    private String password;
    private String confirmPassword;
}
