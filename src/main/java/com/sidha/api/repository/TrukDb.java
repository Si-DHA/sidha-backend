package com.sidha.api.repository;

import com.sidha.api.model.Sopir;
import com.sidha.api.model.Truk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrukDb extends JpaRepository<Truk, UUID> {

    List<Truk> findBySopir(Sopir sopir);
}
