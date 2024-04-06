package com.sidha.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sidha.api.model.Order;

import java.util.UUID;

@Repository
public interface  OrderDb extends JpaRepository<Order, UUID>{  
  
}
