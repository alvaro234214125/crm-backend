package com.dev.backend_crm.controller;

import com.dev.backend_crm.dto.ProductDto;
import com.dev.backend_crm.dto.ProductStatsDto;
import com.dev.backend_crm.entity.PageResponse;
import com.dev.backend_crm.service.ProductService;
import com.dev.backend_crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageResponse<ProductDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Pageable pageable = PageRequest.of(page, size,
                direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());
        return ResponseEntity.ok(productService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody ProductDto dto, Authentication auth) {
        String performedBy = userService.getByEmail(auth.getName()).getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(dto, performedBy));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable Long id, @RequestBody ProductDto dto, Authentication auth) {
        String performedBy = userService.getByEmail(auth.getName()).getName();
        return ResponseEntity.ok(productService.update(id, dto, performedBy));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        String performedBy = userService.getByEmail(auth.getName()).getName();
        productService.delete(id, performedBy);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductDto>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.search(name, type, pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<ProductStatsDto> getStats() {
        return ResponseEntity.ok(productService.getStats());
    }
}
