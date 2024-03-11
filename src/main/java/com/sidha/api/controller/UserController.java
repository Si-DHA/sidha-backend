package com.sidha.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
=======
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
>>>>>>> 67c2ffcde083c6aad2623d701930ce63fb41fe86
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sidha.api.DTO.UserMapper;
import com.sidha.api.DTO.request.EditUserDetailRequestDTO;
import com.sidha.api.DTO.response.BaseResponse;
import com.sidha.api.model.enumerator.Role;
import com.sidha.api.service.UserService;

@RequestMapping("/api/user")
@RestController
public class UserController {
  @Autowired
  private UserService userService;

  @Autowired
  private UserMapper userMapper;

  @GetMapping("/klien")
  public ResponseEntity<?> getKlienList() {
    return ResponseEntity.ok(new BaseResponse<>(true, 200, "User list", userService.getListRole(Role.KLIEN)));
  }

  @GetMapping("/karyawan")
  public ResponseEntity<?> getKaryawanList() {
    return ResponseEntity.ok(new BaseResponse<>(true, 200, "Sopir List", userService.getListRole(Role.KARYAWAN)));
  }

  @GetMapping("/sopir")
  public ResponseEntity<?> getSopirList() {
    return ResponseEntity.ok(new BaseResponse<>(true, 200, "Sopir List", userService.getListRole(Role.SOPIR)));
  }

  @GetMapping("/sopir-no-truk")
  public ResponseEntity<?> getSopirNoTrukList() {
    return ResponseEntity.ok(new BaseResponse<>(true, 200, "Sopir No Truk List", userService.getListSopirNoTruk()));
  }

  @GetMapping("/{id}")
  private ResponseEntity<?> getUserDetail(@PathVariable UUID id) {
    try {
      return ResponseEntity.ok(new BaseResponse<>(true, 200, "User detail",
          userMapper.toGetDetailUserResponseDTO(userService.findById(id))));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new BaseResponse<>(false, 500, e.getMessage(), null));
    }
  }

  @PostMapping("/edit/{id}")
  private ResponseEntity<?> editUserDetail(@RequestParam(required = false) String name,
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
      var userDetail = userMapper.toGetDetailUserResponseDTO(editedUser);
      return ResponseEntity.ok(new BaseResponse<>(true, 200, "User detail edited", userDetail));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new BaseResponse<>(false, 500, e.getMessage(), null));
    }
  }

  @PostMapping("/change-password/{id}")
  private ResponseEntity<?> changePassword(@RequestParam String currentPassword, @RequestParam String newPassword,
      @PathVariable UUID id) {
    try {
      userService.changePassword(currentPassword, newPassword, id);
      return ResponseEntity.ok(new BaseResponse<>(true, 200, "Password changed", null));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new BaseResponse<>(false, 500, e.getMessage(), null));
    }
  }
}
