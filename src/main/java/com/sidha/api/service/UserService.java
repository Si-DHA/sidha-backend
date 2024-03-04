package com.sidha.api.service;

import com.sidha.api.model.UserModel;

public interface UserService {

    UserModel findByEmail(String email);

    UserModel findByUsername(String username);
    
}
