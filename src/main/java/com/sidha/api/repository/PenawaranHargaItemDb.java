package com.sidha.api.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.sidha.api.model.*;

@Repository
public interface PenawaranHargaItemDb extends JpaRepository<PenawaranHargaItem, UUID>{
    @Query("SELECT phi FROM PenawaranHargaItem phi WHERE phi.penawaranHarga.id = :idPenawaranHarga")
    List<PenawaranHargaItem> findByIdPenawaranHarga(@Param("idPenawaranHarga") UUID idPenawaranHarga);

}