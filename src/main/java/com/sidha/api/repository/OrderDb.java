package com.sidha.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.sidha.api.model.order.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface  OrderDb extends JpaRepository<Order, UUID>{
    @Query("SELECT phi FROM Order phi WHERE phi.klien.id = :klien")
    List<Order> findByKlienId(@Param("klien") UUID klien); 

    List<Order> findByKlienIdAndCreatedAtBetween(UUID klienId, LocalDateTime startDateTime, LocalDateTime endDateTime);

}
