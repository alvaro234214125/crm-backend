package com.dev.backend_crm.repository;

import com.dev.backend_crm.entity.Quotation;
import com.dev.backend_crm.entity.QuotationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    long countByStatus(QuotationStatus status);
}
