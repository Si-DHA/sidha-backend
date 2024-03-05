package com.sidha.api.filedata.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sidha.api.filedata.model.ImageData;

import java.util.Optional;

public interface StorageRepository extends JpaRepository<ImageData,Long> {


    Optional<ImageData> findByName(String fileName);
}
