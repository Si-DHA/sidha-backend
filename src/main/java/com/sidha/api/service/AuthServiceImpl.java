package com.sidha.api.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sidha.api.DTO.UserMapper;
import com.sidha.api.DTO.request.LoginUserRequestDTO;
import com.sidha.api.DTO.request.SignUpUserRequestDTO;
import com.sidha.api.DTO.response.UserResponse;
import com.sidha.api.model.Admin;
import com.sidha.api.model.Karyawan;
import com.sidha.api.model.Klien;
import com.sidha.api.model.Sopir;
import com.sidha.api.repository.UserDb;
import com.sidha.api.security.jwt.JwtUtils;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserDb userDb;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserResponse register(SignUpUserRequestDTO<?> request) {
        var user = userMapper.toUserModel(request);
        var jwt = jwtUtils.generateJwtToken(user);
        var UserResponse = new UserResponse();
        UserResponse.setToken(jwt);
        UserResponse.setUser(user);
        saveUser(request);
        return UserResponse;
    }

    private void saveUser(SignUpUserRequestDTO<?> request) {
        switch (request.getRole()) {
            case ADMIN:
                userDb.save(modelMapper.map(request, Admin.class));
                break;
            case KARYAWAN:
                userDb.save(modelMapper.map(request, Karyawan.class));
                break;
            case SOPIR:
                userDb.save(modelMapper.map(request, Sopir.class));
                break;
            case KLIEN:
                userDb.save(modelMapper.map(request, Klien.class));
                break;
            default:
                throw new IllegalArgumentException("Invalid role");
        }
    }

    @Override
    public UserResponse login(LoginUserRequestDTO request) {
        var user = userDb.findByEmail(request.getEmail());
        var jwt = jwtUtils.generateJwtToken(user);
        var UserResponse = new UserResponse();
        UserResponse.setToken(jwt);
        UserResponse.setUser(user);
        return UserResponse;
    }
}
