package com.sidha.api.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.sidha.api.model.UserModel;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {
  Optional<UserModel> findByEmail(String email);

  Optional<UserModel> findByUsername(String username);

  // @Query("SELECT u FROM user_table u WHERE u.id = ?1 AND u.isDeleted = false")
  // Optional<UserModel> findById(UUID id);

  // @Query("SELECT u.username FROM user_table u WHERE u.email = ?1")
  // Optional<String> findUsernameByEmail(String email);
}
