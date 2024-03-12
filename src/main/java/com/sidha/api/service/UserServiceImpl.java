package com.sidha.api.service;

import java.util.UUID;

import org.glassfish.jaxb.core.annotation.OverrideAnnotationOf;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.sidha.api.model.*;
import com.sidha.api.repository.TrukDb;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sidha.api.DTO.UserMapper;
import com.sidha.api.DTO.request.EditUserDetailRequestDTO;
import com.sidha.api.DTO.response.GetUserDetailResponseDTO;
import com.sidha.api.model.enumerator.Role;
import com.sidha.api.repository.UserDb;
import java.util.List;

import static com.sidha.api.model.enumerator.Role.ADMIN;
import static com.sidha.api.model.enumerator.Role.KARYAWAN;
import static com.sidha.api.model.enumerator.Role.KLIEN;
import static com.sidha.api.model.enumerator.Role.SOPIR;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDb userDb;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StorageService storageService;

    @Autowired
    private TrukDb trukDb;
    @Autowired
    private ModelMapper modelMapper;

    @Value("${app.image.url}")
    private String IMAGE_URL;

    @Override
    public UserModel findById(UUID id) {
        return userDb.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserModel findByEmail(String email) {
        return userDb.findByEmail(email);
    }

    @Override
    public UserModel findByUsername(String username) {
        return userDb.findByUsername(username);
    }

    @Override
    public UserModel save(UserModel user) {
        return userDb.save(user);
    }

    @Override
    public GetUserDetailResponseDTO getUserDetail(UUID id) {
        UserModel user = userDb.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        UserModel savedUser = userDb.save(user);
        ImageData imageData = user.getImageData();
        var userDTO = modelMapper.map(user, GetUserDetailResponseDTO.class);
        if (imageData != null) {
            userDTO.setImageUrl(IMAGE_URL + imageData.getName());
        }
        if (user.getRole().equals(Role.KLIEN)) {
           var  user2 = (Klien) user;
            userDTO.setCompanyName(user2.getCompanyName());
            System.out.println(user2.getCompanyName());
        }
        if (user.getRole().equals(Role.KARYAWAN)) {
            var user3 = (Karyawan) user;
            userDTO.setPosition(user3.getPosition());
            System.out.println(user3.getPosition());
        }
        return userDTO;

    }

    @Override
    public UserModel editUserDetail(EditUserDetailRequestDTO requestDTO, UUID id) {
        UserModel user = userDb.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(null != requestDTO.getName() ? requestDTO.getName() : user.getName());
        user.setAddress(null != requestDTO.getAddress() ? requestDTO.getAddress() : user.getAddress());
        user.setPhone(null != requestDTO.getPhone() ? requestDTO.getPhone() : user.getPhone());
        if (null != requestDTO.getImageFile()) {
            try {
                storageService.updateImagaData(requestDTO.getImageFile(), user);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image");
            }
        }

        var userRole = user.getRole();
        if (userRole.equals(Role.ADMIN)) {
            var userAdmin = (Admin) user;
            userAdmin.setSuperAdmin(requestDTO.isSuperAdmin() ? requestDTO.isSuperAdmin() : userAdmin.isSuperAdmin());
            return userDb.save(userAdmin);
        } else if (userRole.equals(Role.KARYAWAN)) {
            var userKaryawan = (Karyawan) user;
            userKaryawan.setPosition(
                    null != requestDTO.getPosition() ? requestDTO.getPosition() : userKaryawan.getPosition());
            return userDb.save(userKaryawan);
        } else {
            return userDb.save(user);
        }
    }

    @Override
    public void changePassword(String currentPassword, String newPassword, UUID id) {
        UserModel user = userDb.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        String userPasswordDb = user.getPassword();

        // Memeriksa apakah password yang diinput saat ini cocok dengan password yang
        // ada di database
        boolean passwordMatch = passwordEncoder.matches(currentPassword, userPasswordDb);

        if (passwordMatch) {
            // Memeriksa apakah password yang baru tidak sama dengan password yang lama
            if (!currentPassword.equals(newPassword)) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userDb.save(user);
            } else {
                throw new RuntimeException("New password cannot be the same as the current password");
            }
        } else {
            throw new RuntimeException("Current password does not match");
        }
    }

    @Override
    public List<UserModel> getListRole(Role role) {
        return userDb.findAllByRole(role);
    }

    @Override
    public List<Sopir> getListSopirNoTruk() {
        List<UserModel> listSopir = userDb.findAllByRole(Role.SOPIR);
        List<Sopir> listSopirNoTruk = new ArrayList<>();
        for (UserModel user : listSopir) {
            if (user.getRole() == SOPIR) {
                Sopir sopirConverted = modelMapper.map(user, Sopir.class);
                if (trukDb.findBySopir(sopirConverted).isEmpty()) {
                    listSopirNoTruk.add(sopirConverted);
                }
            }
        }
        return listSopirNoTruk;
    }
}
