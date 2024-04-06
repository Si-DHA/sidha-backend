package com.sidha.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sidha.api.model.OrderItem;

import java.util.UUID;

@Repository
public interface  OrderItemDb extends JpaRepository<OrderItem, UUID>{

  
}
