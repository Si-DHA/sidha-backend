package com.sidha.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sidha.api.DTO.UserDTO;
import com.sidha.api.model.Admin;
import com.sidha.api.model.Karyawan;
import com.sidha.api.model.UserModel;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import java.util.Optional;
import java.util.NoSuchElementException;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
@RequiredArgsConstructor
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private ModelMapper modelMapper;

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  public UserDetailsService userDetailsService() {
    return new UserDetailsService() {
      @Override
      public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
      }
    };
  }

  public UserModel registerUser(UserDTO userDTO) {

    try {
      userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
      userDTO.setUsername(userDTO.getEmail());
      switch (userDTO.getRole()) {
        case ADMIN:
          Admin admin = modelMapper.map(userDTO, Admin.class);
          logger.info(userDTO.toString());
          return userRepository.save(admin);
        case KARYAWAN:
          Karyawan karyawan = modelMapper.map(userDTO, Karyawan.class);
          return userRepository.save(karyawan);
        case SOPIR:
          UserModel sopir = modelMapper.map(userDTO, UserModel.class);
          return userRepository.save(sopir);
        case KLIEN:
          UserModel klien = modelMapper.map(userDTO, UserModel.class);
          return userRepository.save(klien);
        default:
          throw new IllegalArgumentException("Invalid role");
      }
    } catch (DataIntegrityViolationException e) {
      throw new DataIntegrityViolationException("Email/username sudah pernah digunakan");
    }
  }

}
