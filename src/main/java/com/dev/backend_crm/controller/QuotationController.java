package com.dev.backend_crm.controller;

import com.dev.backend_crm.dto.QuotationDto;
import com.dev.backend_crm.dto.QuotationStatsDto;
import com.dev.backend_crm.entity.PageResponse;
import com.dev.backend_crm.service.QuotationService;
import com.dev.backend_crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quotations")
@RequiredArgsConstructor
public class QuotationController {

    private final QuotationService quotationService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<QuotationDto> create(@RequestBody QuotationDto dto, Authentication auth) {
        String performedBy = userService.getByEmail(auth.getName()).getName();
        return ResponseEntity.ok(quotationService.create(dto, performedBy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuotationDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(quotationService.getById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<QuotationDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Pageable pageable = PageRequest.of(page, size,
                direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());

        return ResponseEntity.ok(quotationService.getAll(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        String performedBy = userService.getByEmail(auth.getName()).getName();
        quotationService.delete(id, performedBy);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<QuotationStatsDto> getStats() {
        return ResponseEntity.ok(quotationService.getStats());
    }
}
