package com.sidha.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sidha.api.model.order.OrderItem;

import java.util.List;
import java.util.UUID;

@Repository
public interface  OrderItemDb extends JpaRepository<OrderItem, UUID>{

    List<OrderItem> findByStatusOrder(int i);
  
}
