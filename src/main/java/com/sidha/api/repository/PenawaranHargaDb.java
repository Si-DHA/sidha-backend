package com.sidha.api.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.sidha.api.model.*;

@Repository
public interface PenawaranHargaDb extends JpaRepository<PenawaranHarga, UUID> {
    @Query("SELECT phi FROM PenawaranHarga phi WHERE phi.klien.id = :klien")
    PenawaranHarga findByIdKlien(@Param("klien") UUID klien);

}