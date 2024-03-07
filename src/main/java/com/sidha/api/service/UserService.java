package com.sidha.api.service;

import java.util.UUID;

import com.sidha.api.DTO.request.EditUserDetailRequestDTO;
import com.sidha.api.DTO.response.GetUserDetailResponseDTO;
import com.sidha.api.model.UserModel;

public interface UserService {

    UserModel findByEmail(String email);

    UserModel findByUsername(String username);

    UserModel findById(UUID id);

    GetUserDetailResponseDTO getUserDetail(UUID id);

    void editUserDetail( EditUserDetailRequestDTO requestDTO, UUID id);

    void changePassword(String currentPassword, String newPassword, UUID id);
}
