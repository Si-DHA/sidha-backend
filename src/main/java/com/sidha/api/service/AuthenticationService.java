package com.sidha.api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import com.sidha.api.repository.UserDb;
import lombok.RequiredArgsConstructor;

import com.sidha.api.DTO.UserDTO;
import com.sidha.api.DTO.response.UserResponse;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

      private final UserDb userRepository;
      private final UserService userService;
      private final PasswordEncoder passwordEncoder;
      private final JwtService jwtService;
      private final AuthenticationManager authenticationManager;
      private final ModelMapper modelMapper;

      public UserResponse signup(UserDTO request) {
            var user = userService.registerUser(request);
            var jwt = jwtService.generateToken(user);
            var UserResponse = new UserResponse();
            UserResponse.setToken(jwt);
            UserResponse.setUser(request);
            return UserResponse;

      }
}
