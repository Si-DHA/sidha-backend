package com.sidha.api.service;

import java.util.UUID;

import com.sidha.api.DTO.request.EditUserDetailRequestDTO;
import com.sidha.api.DTO.response.GetUserDetailResponseDTO;
import com.sidha.api.model.Sopir;
import com.sidha.api.model.UserModel;
import com.sidha.api.model.enumerator.Role;
import java.util.List;

public interface UserService {

    UserModel findByEmail(String email);

    UserModel findByUsername(String username);

    UserModel findById(UUID id);

    UserModel save(UserModel user);

    GetUserDetailResponseDTO getUserDetail(UUID id);

    UserModel editUserDetail(EditUserDetailRequestDTO requestDTO, UUID id);

    void changePassword(String currentPassword, String newPassword, UUID id);

    List<UserModel> getListRole(Role role);

    List<Sopir> getListSopirNoTruk();
    
    List<UserModel> findAllList();
}
