package com.dev.backend_crm.service;

import com.dev.backend_crm.dto.InteractionDto;
import com.dev.backend_crm.entity.PageResponse;
import com.dev.backend_crm.dto.InteractionStatsDto;

import org.springframework.data.domain.Pageable;

public interface InteractionService {
    PageResponse<InteractionDto> getAll(Pageable pageable);
    InteractionDto getById(Long id);
    InteractionDto create(InteractionDto dto, String performedBy);
    InteractionDto update(Long id, InteractionDto dto, String performedBy);
    void delete(Long id, String performedBy);
    InteractionStatsDto getStats();
}
