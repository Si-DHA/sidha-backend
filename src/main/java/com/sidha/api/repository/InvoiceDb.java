package com.sidha.api.repository;

import com.sidha.api.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InvoiceDb extends JpaRepository<Invoice, UUID> {
}
