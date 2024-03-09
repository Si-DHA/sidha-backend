package com.sidha.api.service;

import com.sidha.api.model.UserModel;
import com.sidha.api.model.enumerator.Role;

import java.util.List;


public interface UserService {

    UserModel findByEmail(String email);

    UserModel findByUsername(String username);
    List<UserModel> findByRole(Role role);
    
}
