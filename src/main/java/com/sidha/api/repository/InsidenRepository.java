package com.sidha.api.repository;

import com.sidha.api.model.Insiden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InsidenRepository extends JpaRepository<Insiden, UUID> {
    // Method for fetching incidents by status
    List<Insiden> findByStatus(Insiden.InsidenStatus status);

    List<Insiden> findAllByIsDeletedFalse();
}
