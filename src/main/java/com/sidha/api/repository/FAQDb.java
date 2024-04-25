package com.sidha.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sidha.api.model.FAQ;

@Repository
public interface FAQDb extends JpaRepository<FAQ, Long> {
}
