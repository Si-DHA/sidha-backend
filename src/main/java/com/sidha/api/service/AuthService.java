package com.sidha.api.service;

import com.sidha.api.DTO.request.ForgotPassUserRequestDTO;
import com.sidha.api.DTO.request.LoginUserRequestDTO;
import com.sidha.api.DTO.request.SignUpUserRequestDTO;
import com.sidha.api.DTO.response.UserResponse;

public interface AuthService {
    UserResponse register(SignUpUserRequestDTO request);

    UserResponse login(LoginUserRequestDTO request);

    String forgotPassword(ForgotPassUserRequestDTO request);

    void resetPassword(String token, String password);
    
}
