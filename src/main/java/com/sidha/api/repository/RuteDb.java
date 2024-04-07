package com.sidha.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sidha.api.model.order.Rute;

@Repository
public interface RuteDb extends JpaRepository<Rute, Long>{
    
}
