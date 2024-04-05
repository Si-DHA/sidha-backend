package com.sidha.api.repository;

import java.util.List;
import java.util.Optional;

import com.sidha.api.model.ProfileImage;
import com.sidha.api.model.enumerator.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sidha.api.model.ImageData;
import com.sidha.api.model.UserModel;
import java.util.UUID;

@Repository
public interface ImageDataRepository extends JpaRepository<ImageData, Long> {
  Optional<ImageData> findByName(String name);

  @Query("SELECT i FROM ProfileImage i WHERE i.user.id = :userId")
  Optional<ProfileImage> findProfileByUserId(UUID userId);

}
