package com.sidha.api.service;

import com.sidha.api.model.UserModel;
import java.util.List;
public interface UserService {

    UserModel findByEmail(String email);

    UserModel findByUsername(String username);
    List<UserModel> findByRole(String role);
    
}
