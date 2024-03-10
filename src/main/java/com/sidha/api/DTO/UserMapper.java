package com.sidha.api.DTO;

import org.mapstruct.Mapper;

import com.sidha.api.DTO.request.SignUpUserRequestDTO;
import com.sidha.api.DTO.response.GetUserDetailResponseDTO;
import com.sidha.api.model.UserModel;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserModel toUserModel(SignUpUserRequestDTO request);

    GetUserDetailResponseDTO toGetDetailUserResponseDTO(UserModel user);
}
