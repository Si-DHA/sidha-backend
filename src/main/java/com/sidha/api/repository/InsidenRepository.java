package com.sidha.api.repository;

import com.sidha.api.model.Insiden;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface InsidenRepository extends JpaRepository<Insiden, UUID> {
}
