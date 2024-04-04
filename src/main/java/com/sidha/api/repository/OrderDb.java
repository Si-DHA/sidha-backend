package com.sidha.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sidha.api.model.Order;
import com.sidha.api.model.enumerator.StatusOrder;

import java.util.List;
import java.util.UUID;

@Repository
public interface  OrderDb extends JpaRepository<Order, UUID>{

  List<Order> findByStatus(StatusOrder status);
  
  
}
