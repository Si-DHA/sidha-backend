package com.sidha.api.service;

import com.sidha.api.DTO.request.SignUpUserRequestDTO;
import com.sidha.api.DTO.response.UserResponse;

public interface AuthService {
    UserResponse register(SignUpUserRequestDTO<?> request);
    
}
