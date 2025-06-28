package com.dev.backend_crm.service;

import com.dev.backend_crm.dto.QuotationDto;
import com.dev.backend_crm.dto.QuotationStatsDto;
import com.dev.backend_crm.entity.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuotationService {
    QuotationDto create(QuotationDto dto, String performedBy);
    QuotationDto getById(Long id);
    PageResponse<QuotationDto> getAll(Pageable pageable);
    void delete(Long id, String performedBy);
    QuotationStatsDto getStats();
}