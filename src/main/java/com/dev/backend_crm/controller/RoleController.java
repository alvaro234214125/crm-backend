package com.dev.backend_crm.controller;

import com.dev.backend_crm.dto.RoleDto;
import com.dev.backend_crm.entity.PageResponse;
import com.dev.backend_crm.entity.Role;
import com.dev.backend_crm.repository.RoleRepository;
import com.dev.backend_crm.service.RoleService;
import com.dev.backend_crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<RoleDto>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Pageable pageable = PageRequest.of(
                page, size,
                direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending()
        );

        return ResponseEntity.ok(roleService.findAll(pageable));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getRole(@PathVariable Long id) {
        return roleService.findRoleById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RoleDto> create(@RequestBody RoleDto roleDto, Authentication auth) {
        String email = userService.getByEmail(auth.getName()).getName();
        RoleDto createdRole = roleService.save(roleDto, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RoleDto> update(@PathVariable Long id, @RequestBody RoleDto roleDto, Authentication auth) {
        String email = userService.getByEmail(auth.getName()).getName();
        return roleService.findRoleById(id)
                .map(existing -> {
                    roleDto.setId(id);
                    RoleDto updated = roleService.save(roleDto, email);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth){
        if (!roleService.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        roleService.remove(id, userService.getByEmail(auth.getName()).getName());
        return ResponseEntity.noContent().build();
    }
}
