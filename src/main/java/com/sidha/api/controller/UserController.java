package com.sidha.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sidha.api.DTO.UserDTO;
import com.sidha.api.DTO.response.UserResponse;
import com.sidha.api.service.AuthenticationService;

@RestController
@RequestMapping("/api/user")
public class UserController {
  @Autowired
  private AuthenticationService authenticationService;
  
  @PostMapping("/signup")
  public UserResponse  signup(@RequestBody UserDTO  request) {
    return authenticationService.signup(request);
  }

}
