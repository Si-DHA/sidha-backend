package com.sidha.api.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sidha.api.model.*;

@Repository
public interface PenawaranHargaDb extends JpaRepository<PenawaranHarga, UUID>{
}