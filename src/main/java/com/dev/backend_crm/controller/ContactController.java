package com.dev.backend_crm.controller;

import com.dev.backend_crm.dto.ContactDto;
import com.dev.backend_crm.dto.ContactStatsDto;
import com.dev.backend_crm.service.ContactService;
import com.dev.backend_crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;
    private final UserService userService;

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ContactDto>> getByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(contactService.getByClientId(clientId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ContactDto> create(@RequestBody ContactDto dto, Authentication auth) {
        String performedBy = userService.getByEmail(auth.getName()).getName();
        return ResponseEntity.ok(contactService.create(dto, performedBy));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactDto> update(@PathVariable Long id, @RequestBody ContactDto dto, Authentication auth) {
        String performedBy = userService.getByEmail(auth.getName()).getName();
        return ResponseEntity.ok(contactService.update(id, dto, performedBy));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        String performedBy = userService.getByEmail(auth.getName()).getName();
        contactService.delete(id, performedBy);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<ContactStatsDto> getStats() {
        return ResponseEntity.ok(contactService.getContactStats());
    }

}
