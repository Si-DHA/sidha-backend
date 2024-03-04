package com.sidha.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sidha.api.model.UserModel;

import java.util.UUID;

@Repository
public interface UserDb extends JpaRepository<UserModel, UUID>{
    UserModel findByUsername(String username);

    UserModel findByEmail(String email);

    UserModel findByToken(String token);   
}