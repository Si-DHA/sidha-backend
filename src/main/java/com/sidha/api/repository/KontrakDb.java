package com.sidha.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sidha.api.model.Kontrak;
import com.sidha.api.model.user.UserModel;

@Repository
public interface KontrakDb extends JpaRepository<Kontrak, Long> {
  Optional<Kontrak> findByName(String name);

  Optional<Kontrak> findByUser(UserModel user);
}
