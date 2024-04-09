package com.sidha.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sidha.api.model.enumerator.Role;
import com.sidha.api.model.user.UserModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDb extends JpaRepository<UserModel, UUID> {
    @Query("SELECT u FROM UserModel u WHERE u.username = ?1 AND u.isDeleted = false")
    UserModel findByUsername(String username);

    @Query("SELECT u FROM UserModel u WHERE u.email = ?1 AND u.isDeleted = false")
    UserModel findByEmail(String email);

    @Query("SELECT u FROM UserModel u WHERE u.token = ?1 AND u.isDeleted = false")
    UserModel findByToken(String token);
    
    @Query("SELECT u FROM UserModel u WHERE u.role = ?1 AND ( u.isDeleted = false OR u.isDeleted = true )")
    List<UserModel> findAllByRole(Role role);

    @SuppressWarnings("null")
    List<UserModel> findAll();

    @SuppressWarnings("null")
    @Query("SELECT u FROM UserModel u WHERE u.id = ?1 AND u.isDeleted = false")
    Optional<UserModel> findById(UUID id);

    @Query("SELECT u FROM UserModel u WHERE u.id =?1")
    Optional<UserModel> getDetailUserById(UUID id);
}   