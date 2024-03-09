package com.sidha.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sidha.api.DTO.response.BaseResponse;
import com.sidha.api.model.enumerator.Role;
import com.sidha.api.service.UserService;

@RequestMapping("/api/user")
@RestController
public class UserController {
  @Autowired
  private UserService userService;

  // @Autowired
  // private UserMapper userMapper;

  @GetMapping("/klien")
  public BaseResponse<?> getMethodName() {
      return new BaseResponse<>(true, 200, "User list", userService.findByRole(Role.KLIEN));
  }
  
}
