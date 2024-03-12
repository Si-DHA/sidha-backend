package com.sidha.api.DTO;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidha.api.DTO.request.SignUpUserRequestDTO;
import com.sidha.api.DTO.response.GetUserDetailResponseDTO;
import com.sidha.api.model.Admin;
import com.sidha.api.model.Karyawan;
import com.sidha.api.model.Klien;
import com.sidha.api.model.Sopir;
import com.sidha.api.model.UserModel;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserModel toUserModel(SignUpUserRequestDTO request);

    GetUserDetailResponseDTO toGetDetailUserResponseDTO(UserModel user);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "isSuperAdmin", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "companyName", ignore = true)
    @Mapping(target = "isAvailable", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "kontrakUrl", ignore = true)
    GetUserDetailResponseDTO getUserDetail(UserModel user);

    @AfterMapping
    default void getUserDetail(UserModel user, @MappingTarget GetUserDetailResponseDTO getUserDetail) {
        if (user.getImageData() != null) {
            getUserDetail.setImageUrl(user.getImageData().getName());
        }
    
        if (user.getKontrak() != null) {
            getUserDetail.setKontrakUrl(user.getKontrak().getName());
        }
        var userRole = user.getRole();
        switch (userRole) {
            case KLIEN:
                if (user instanceof Klien) {
                    Klien klien = (Klien) user;
                    getUserDetail.setCompanyName(klien.getCompanyName());
                }
                break;
            case KARYAWAN:
                if (user instanceof Karyawan) {
                    Karyawan karyawan = (Karyawan) user;
                    getUserDetail.setPosition(karyawan.getPosition());
                }
                break;
            case ADMIN:
                if (user instanceof Admin) {
                    Admin admin = (Admin) user;
                    getUserDetail.setSuperAdmin(admin.isSuperAdmin());
                }
                break;
            case SOPIR:
                if (user instanceof Sopir) {
                    Sopir sopir = (Sopir) user;
                    getUserDetail.setAvailable(sopir.isAvailable());
                }
                break;
        }
    }
}
