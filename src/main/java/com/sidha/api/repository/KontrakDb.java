package com.sidha.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.sidha.api.model.Kontrak;
import com.sidha.api.model.user.UserModel;
import java.util.List;
import java.util.Optional;

@Repository
public interface KontrakDb extends JpaRepository<Kontrak, Long> {
  Optional<Kontrak> findByName(String name);

  Optional<Kontrak> findByUser(UserModel user);

  @Query("SELECT k FROM Kontrak k WHERE k.user.role = 'KLIEN'")
  List<Kontrak> findAllClientContract();
}
