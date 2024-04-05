package com.sidha.api.repository;

import java.util.Optional;

import com.sidha.api.model.image.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sidha.api.model.image.ImageData;

import java.util.UUID;

@Repository
public interface ImageDataRepository extends JpaRepository<ImageData, Long> {
  Optional<ImageData> findByName(String name);

  @Query("SELECT i FROM ProfileImage i WHERE i.user.id = :userId")
  Optional<ProfileImage> findProfileByUserId(UUID userId);

}
