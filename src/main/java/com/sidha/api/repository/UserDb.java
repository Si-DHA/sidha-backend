package com.sidha.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sidha.api.model.UserModel;
// import com.sidha.api.model.enumerator.Role;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserDb extends JpaRepository<UserModel, UUID> {
    UserModel findByUsername(String username);

    UserModel findByEmail(String email);

    UserModel findByToken(String token);

    // List<UserModel> findAllByRole(Role role);

    List<UserModel> findAll();
}