package com.dev.backend_crm.service;

import com.dev.backend_crm.dto.InvoiceDto;
import com.dev.backend_crm.dto.InvoiceStatsDto;
import com.dev.backend_crm.entity.PageResponse;
import org.springframework.data.domain.Pageable;

public interface InvoiceService {
    InvoiceDto create(InvoiceDto dto, String performedBy);
    InvoiceDto getById(Long id);
    PageResponse<InvoiceDto> getAll(Pageable pageable);
    void delete(Long id, String performedBy);
    InvoiceStatsDto getStats();
}