package com.sidha.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sidha.api.DTO.request.ForgotPassUserRequestDTO;
import com.sidha.api.DTO.request.LoginUserRequestDTO;
import com.sidha.api.DTO.request.SignUpUserRequestDTO;
import com.sidha.api.DTO.response.BaseResponse;
import com.sidha.api.DTO.response.UserResponse;
import com.sidha.api.model.UserModel;
import com.sidha.api.model.enumerator.Role;
import com.sidha.api.service.AuthService;
import com.sidha.api.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  private AuthService authService;

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @PostMapping(value = "/register")
  public BaseResponse<UserResponse> register(
      @RequestParam String name, @RequestParam(required = false) String password, @RequestParam String email,
      @RequestParam String role,
      @RequestParam String address, @RequestParam String phone,
      @RequestParam(required = false) String position, @RequestParam(required = false) String companyName,
      @RequestPart(required = false) MultipartFile imageFile) {
    try {
      SignUpUserRequestDTO request = new SignUpUserRequestDTO();
      request.setName(name);
      request.setPassword(password);
      request.setEmail(email);
      request.setRole(Role.valueOf(role));
      request.setAddress(address);
      request.setPhone(phone);
      request.setPosition(position);
      request.setCompanyName(companyName);
      request.setImageFile(imageFile);

      // Jika field kosong, maka akan mengembalikan response error
      if (request.getEmail().isEmpty() || password == null) {
        return new BaseResponse<>(false, 400, "Email and password cannot be empty", null);
      }

      request.setUsername(request.getEmail().split("@")[0]);
      if (userService.findByUsername(request.getUsername()) != null) {
        return new BaseResponse<>(false, 400, "Username already exists", null);
      }

      UserResponse response = authService.register(request);
      return new BaseResponse<>(true, 200, "User registered successfully", response);
    } catch (Exception e) {
      return new BaseResponse<>(false, 500, e.getMessage(), null);
    }
  }

  @PostMapping("/login")
  public BaseResponse<UserResponse> login(@RequestBody LoginUserRequestDTO request) {
    try {
      // Jika field kosong, maka akan mengembalikan response error
      if (request.getEmail().isEmpty() || request.getPassword().isEmpty()) {
        return new BaseResponse<>(false, 400, "Email and password cannot be empty", null);
      }

      UserModel user = userService.findByEmail(request.getEmail());
      if (user == null) {
        return new BaseResponse<>(false, 400, "User not found", null);
      }

      if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        return new BaseResponse<>(false, 400, "Invalid password", null);
      }

      UserResponse response = authService.login(request);
      return new BaseResponse<>(true, 200, "User logged in successfully", response);
    } catch (Exception e) {
      return new BaseResponse<>(false, 500, e.getMessage(), null);
    }
  }

  @PostMapping("/forgot-password")
  public BaseResponse<?> forgotPassword(@RequestBody ForgotPassUserRequestDTO request) {
    try {
      // Jika field kosong, maka akan mengembalikan response error
      if (request.getEmail().isEmpty()) {
        return new BaseResponse<>(false, 400, "Email cannot be empty", null);
      }

      if (userService.findByEmail(request.getEmail()) == null) {
        return new BaseResponse<>(false, 400, "User not found", null);
      }

      authService.forgotPassword(request);
      return new BaseResponse<>(true, 200, "Reset password link sent to your email", null);
    } catch (Exception e) {
      return new BaseResponse<>(false, 500, e.getMessage(), null);
    }
  }

  @PostMapping("/reset-password")
  public BaseResponse<?> resetPass(@RequestParam String token, @RequestParam String password) {
    try {
      // Jika field kosong, maka akan mengembalikan response error
      if (token.isEmpty() || password.isEmpty()) {
        return new BaseResponse<>(false, 400, "Token and password cannot be empty", null);
      }

      authService.resetPassword(token, passwordEncoder.encode(password));
      return new BaseResponse<>(true, 200, "Password reset successfully", null);
    } catch (Exception e) {
      return new BaseResponse<>(false, 500, e.getMessage(), null);
    }
  }

}
