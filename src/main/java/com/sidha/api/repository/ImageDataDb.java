package com.sidha.api.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sidha.api.model.ImageData;
import com.sidha.api.model.UserModel;
import java.util.UUID;

@Repository
public interface ImageDataDb extends JpaRepository<ImageData, Long> {
  Optional<ImageData> findByName(String name);

  Optional<ImageData> findByUser(UserModel user);

  @Query("SELECT i FROM ImageData i WHERE i.user.id = :userId")
  Optional<ImageData> findByUserId(UUID userId);
}
