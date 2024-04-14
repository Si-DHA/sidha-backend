package com.sidha.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.sidha.api.model.order.OrderItem;
import java.util.List;
import java.util.UUID;

@Repository
public interface  OrderItemDb extends JpaRepository<OrderItem, UUID>{
    @Query("SELECT phi FROM OrderItem phi WHERE phi.sopir.id = :sopir")
    List<OrderItem> findByIdSopir(@Param("sopir") UUID sopir);
  
    @Query("SELECT phi FROM OrderItem phi WHERE phi.order.id = :idOrder")
    List<OrderItem> findByIdOrder(@Param("idOrder") UUID idOrder);
}
