package com.dev.backend_crm.service;

import com.dev.backend_crm.dto.PaymentDto;
import com.dev.backend_crm.dto.PaymentStatsDto;
import com.dev.backend_crm.entity.PageResponse;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PaymentDto create(PaymentDto dto, String performedBy);
    PaymentDto getById(Long id);
    PageResponse<PaymentDto> getAll(Pageable pageable);
    void delete(Long id, String performedBy);
    PaymentStatsDto getStats();
}