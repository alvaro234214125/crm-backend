package com.dev.backend_crm.controller;

import com.dev.backend_crm.dto.InteractionDto;
import com.dev.backend_crm.dto.InteractionStatsDto;
import com.dev.backend_crm.entity.PageResponse;
import com.dev.backend_crm.service.InteractionService;
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
@RequestMapping("/interactions")
@RequiredArgsConstructor
public class InteractionController {

    private final InteractionService interactionService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageResponse<InteractionDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Pageable pageable = PageRequest.of(page, size,
                direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());
        return ResponseEntity.ok(interactionService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InteractionDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(interactionService.getById(id));
    }

    @PostMapping
    public ResponseEntity<InteractionDto> create(@RequestBody InteractionDto dto, Authentication auth) {
        String performedBy = userService.getByEmail(auth.getName()).getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(interactionService.create(dto, performedBy));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InteractionDto> update(@PathVariable Long id, @RequestBody InteractionDto dto, Authentication auth) {
        String performedBy = userService.getByEmail(auth.getName()).getName();
        return ResponseEntity.ok(interactionService.update(id, dto, performedBy));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        String performedBy = userService.getByEmail(auth.getName()).getName();
        interactionService.delete(id, performedBy);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<InteractionStatsDto> getStats() {
        return ResponseEntity.ok(interactionService.getStats());
    }
}
