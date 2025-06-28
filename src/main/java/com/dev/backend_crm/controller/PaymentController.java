package com.dev.backend_crm.controller;

import com.dev.backend_crm.dto.PaymentDto;
import com.dev.backend_crm.dto.PaymentStatsDto;
import com.dev.backend_crm.entity.PageResponse;
import com.dev.backend_crm.service.PaymentService;
import com.dev.backend_crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<PaymentDto> create(@RequestBody PaymentDto dto, Authentication auth) {
        String performedBy = userService.getByEmail(auth.getName()).getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(dto, performedBy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<PaymentDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(paymentService.getAll(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        String performedBy = userService.getByEmail(auth.getName()).getName();
        paymentService.delete(id, performedBy);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<PaymentStatsDto> getStats() {
        return ResponseEntity.ok(paymentService.getStats());
    }
}
