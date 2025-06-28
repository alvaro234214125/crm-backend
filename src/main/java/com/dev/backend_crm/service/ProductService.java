package com.dev.backend_crm.service;

import com.dev.backend_crm.dto.ProductDto;
import com.dev.backend_crm.dto.ProductStatsDto;
import com.dev.backend_crm.entity.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    PageResponse<ProductDto> getAll(Pageable pageable);
    ProductDto getById(Long id);
    ProductDto create(ProductDto dto, String performedBy);
    ProductDto update(Long id, ProductDto dto, String performedBy);
    void delete(Long id, String performedBy);
    PageResponse<ProductDto> search(String name, String type, Pageable pageable);
    ProductStatsDto getStats();
}
