package com.sidha.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sidha.api.model.UserModel;
import com.sidha.api.repository.UserDb;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDb userDb;

    @Override
    public UserModel findByEmail(String email) {
        return userDb.findByEmail(email);
    }
    
}
