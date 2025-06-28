package com.dev.backend_crm.repository;

import com.dev.backend_crm.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Query("SELECT SUM(i.total) FROM Invoice i")
    Double getTotalRevenue();

    @Query("SELECT COUNT(DISTINCT i.client.id) FROM Invoice i")
    Long getDistinctClientCount();
}