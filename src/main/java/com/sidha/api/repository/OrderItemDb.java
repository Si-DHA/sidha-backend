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

    List<OrderItem> findByStatusOrder(int i);
  
    @Query("SELECT phi FROM OrderItem phi WHERE phi.order.id = :idOrder")
    List<OrderItem> findByIdOrder(@Param("idOrder") UUID idOrder);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.klien.id = :klienId AND oi.statusOrder NOT IN (-1, 0)")
    List<OrderItem> findByKlienIdAndStatusNotIn(@Param("klienId") UUID klienId);

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.order.klien.id = :klienId AND oi.statusOrder = 5")
    int countCompletedOrderItemsByKlienId(@Param("klienId") UUID klienId);
}
