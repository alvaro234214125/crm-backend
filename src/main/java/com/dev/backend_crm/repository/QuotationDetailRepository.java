package com.dev.backend_crm.repository;

import com.dev.backend_crm.entity.QuotationDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuotationDetailRepository extends JpaRepository<QuotationDetail, Long> {
    List<QuotationDetail> findByQuotationId(Long quotationId);
}