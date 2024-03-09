package com.sidha.api.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sidha.api.DTO.UserMapper;
import com.sidha.api.DTO.response.BaseResponse;
import com.sidha.api.model.UserModel;
import com.sidha.api.service.UserService;
import java.util.*;

@RequestMapping("/api/user")
@RestController
public class UserController {
  @Autowired
  private UserService userService;

  @Autowired
  private UserMapper userMapper;

  @GetMapping("/klien")
  public BaseResponse<List<UserModel>> getMethodName(@RequestParam String param) {
      return new BaseResponse<>(true, 200, "User list", userService.findByRole("KLIEN"));
  }
  
}
