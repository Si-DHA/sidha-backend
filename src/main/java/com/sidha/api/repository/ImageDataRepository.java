package com.sidha.api.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sidha.api.model.ImageData;
import java.util.List;
import com.sidha.api.model.UserModel;


@Repository
public interface ImageDataRepository extends JpaRepository<ImageData, Long> {
  Optional<ImageData> findByName(String name);
  Optional<ImageData> findByUser(UserModel user);
}
