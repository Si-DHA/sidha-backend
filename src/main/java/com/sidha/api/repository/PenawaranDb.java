package com.sidha.api.repository;

import com.sidha.api.model.Penawaran;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PenawaranDb extends JpaRepository<Penawaran, UUID> {
    
}
