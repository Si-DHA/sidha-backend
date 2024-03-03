package com.sidha.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sidha.api.DTO.request.SignUpUserRequestDTO;
import com.sidha.api.DTO.response.BaseResponse;
import com.sidha.api.DTO.response.UserResponse;
import com.sidha.api.service.AuthService;

@RestController
@RequestMapping("/api/user")
public class AuthController {
  @Autowired
  private AuthService authService;
  
  @PostMapping("/register")
  public BaseResponse<UserResponse> register(@RequestBody SignUpUserRequestDTO<?>  request) {
    try {
      UserResponse response = authService.register(request);
      return new BaseResponse<UserResponse>(true, 200, "User registered successfully", response);
    } catch (Exception e) {
      return new BaseResponse<UserResponse>(false, 500, e.getMessage(), null);
    }
  }

}
