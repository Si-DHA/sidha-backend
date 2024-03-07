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
import com.sidha.api.DTO.request.EditUserDetailRequestDTO;
import com.sidha.api.DTO.response.BaseResponse;
import com.sidha.api.DTO.response.GetUserDetailResponseDTO;
import com.sidha.api.service.UserService;

@RequestMapping("/api/user")
@RestController
public class UserController {
  @Autowired
  private UserService userService;

  @Autowired
  private UserMapper userMapper;
  
  @GetMapping("/{id}")
  private BaseResponse<GetUserDetailResponseDTO> getUserDetail(@PathVariable UUID id) {
    try {
      return new BaseResponse<>(true, 200, "User detail", userService.getUserDetail(id));
    } catch (Exception e) {
      return new BaseResponse<>(false, 500, e.getMessage(), null);
    }
  }

  @PostMapping("/edit/{id}")
  private BaseResponse<?>  editUserDetail(@RequestParam(required = false) String name,
      @RequestParam String address, @RequestParam String phone,
      @RequestParam(required = false) String position, @RequestParam(required = false) boolean isSuperAdmin, 
      @RequestPart(required = false) MultipartFile imageFile, @PathVariable UUID id) {
    try {
      EditUserDetailRequestDTO request = new EditUserDetailRequestDTO();
      request.setName(name);
      request.setAddress(address);
      request.setPhone(phone);
      request.setPosition(position);
      request.setSuperAdmin(isSuperAdmin);
      request.setImageFile(imageFile);
      var editedUser = userService.editUserDetail(request, id);
      var userDetail =  userMapper.toGetDetailUserResponseDTO(editedUser);
      return new BaseResponse<>(true, 200, "User edited successfully",userDetail );
    } catch (Exception e) {
      return new BaseResponse<>(false, 500, e.getMessage(), null);
    }
  }


  @PostMapping("/change-password/{id}")
  private BaseResponse<?> changePassword(@RequestParam String currentPassword, @RequestParam String newPassword, @PathVariable UUID id) {
    try {
      userService.changePassword(currentPassword, newPassword, id);
      return new BaseResponse<>(true, 200, "Password changed successfully", null);
    } catch (Exception e) {
      return new BaseResponse<>(false, 500, e.getMessage(), null);
    }
  }
}
