package com.dev.backend_crm.controller;

import com.dev.backend_crm.dto.RoleDto;
import com.dev.backend_crm.dto.UserDto;
import com.dev.backend_crm.dto.UserListDto;
import com.dev.backend_crm.dto.UserStatsDto;
import com.dev.backend_crm.entity.PageResponse;
import com.dev.backend_crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<UserListDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Pageable pageable = PageRequest.of(
          page, size,
          direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending()
        );
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UserListDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto, Authentication auth) {
        return userService.register(userDto, userService.getByEmail(auth.getName()).getName());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto, Authentication auth) {
        return userService.updateUser(id, userDto, userService.getByEmail(auth.getName()).getName());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication auth) {
        userService.deleteUser(id, userService.getByEmail(auth.getName()).getName());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public ResponseEntity<UserStatsDto> getStats() {
        return ResponseEntity.ok(userService.getUserStats());
    }
}