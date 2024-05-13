package com.sidha.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.sidha.api.model.order.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface  OrderDb extends JpaRepository<Order, String>{
    @Query("SELECT phi FROM Order phi WHERE phi.klien.id = :klien")
    List<Order> findByKlienId(@Param("klien") UUID klien);

    @Query("SELECT o FROM Order o JOIN o.orderItems oi WHERE oi.id = :orderItemId")
    Optional<Order> findByOrderItemId(@Param("orderItemId") String orderItemId);

    List<Order> findByKlienIdAndCreatedAtBetween(UUID klienId, LocalDateTime startDateTime, LocalDateTime endDateTime);

}
