package com.dev.backend_crm.service.impl;

import com.dev.backend_crm.dto.ProductDto;
import com.dev.backend_crm.dto.ProductStatsDto;
import com.dev.backend_crm.entity.PageResponse;
import com.dev.backend_crm.entity.Product;
import com.dev.backend_crm.repository.ProductRepository;
import com.dev.backend_crm.service.ActivityLogService;
import com.dev.backend_crm.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ActivityLogService activityLogService;

    @Override
    public PageResponse<ProductDto> getAll(Pageable pageable) {
        Page<Product> page = productRepository.findAll(pageable);
        List<ProductDto> content = page.getContent().stream().map(this::toDto).collect(Collectors.toList());

        return new PageResponse<>(content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Override
    public ProductDto getById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toDto(p);
    }

    @Override
    public ProductDto create(ProductDto dto, String performedBy) {
        Product saved = productRepository.save(fromDto(dto));
        activityLogService.log("CREATE_PRODUCT", "Creó el producto: " + saved.getName(), "Product", saved.getId(), performedBy);
        return toDto(saved);
    }

    @Override
    public ProductDto update(Long id, ProductDto dto, String performedBy) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setType(dto.getType());

        Product updated = productRepository.save(existing);
        activityLogService.log("UPDATE_PRODUCT", "Actualizó el producto: " + updated.getName(), "Product", updated.getId(), performedBy);
        return toDto(updated);
    }

    @Override
    public void delete(Long id, String performedBy) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
        activityLogService.log("DELETE_PRODUCT", "Eliminó el producto con ID: " + id, "Product", id, performedBy);
    }

    @Override
    public PageResponse<ProductDto> search(String name, String type, Pageable pageable) {
        Specification<Product> spec = (root, query, cb) -> cb.conjunction();

        if (name != null && !name.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (type != null && !type.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }

        Page<Product> page = productRepository.findAll(spec, pageable);
        List<ProductDto> content = page.getContent().stream().map(this::toDto).collect(Collectors.toList());

        return new PageResponse<>(content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Override
    public ProductStatsDto getStats() {
        List<Product> all = productRepository.findAll();

        long total = all.size();
        long tiposUnicos = all.stream().map(Product::getType).distinct().count();
        double precioPromedio = all.stream().mapToDouble(Product::getPrice).average().orElse(0.0);

        return new ProductStatsDto(total, tiposUnicos, precioPromedio);
    }

    private ProductDto toDto(Product p) {
        return ProductDto.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .type(p.getType())
                .build();
    }

    private Product fromDto(ProductDto dto) {
        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .type(dto.getType())
                .build();
    }
}
