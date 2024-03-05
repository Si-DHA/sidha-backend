package com.sidha.api.filedata.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sidha.api.filedata.model.FileData;

public interface FileDataRepository extends JpaRepository<FileData,Integer> {
    Optional<FileData> findByName(String fileName);
}
