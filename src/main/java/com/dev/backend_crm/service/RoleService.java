package com.dev.backend_crm.service;

import com.dev.backend_crm.dto.RoleDto;
import com.dev.backend_crm.entity.PageResponse;
import com.dev.backend_crm.entity.Role;
import com.dev.backend_crm.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public PageResponse<RoleDto> findAll(Pageable pageable) {
        Page<Role> page = roleRepository.findAll(pageable);

        List<RoleDto> content = page.getContent()
                .stream()
                .map(this::roleToDto)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    public RoleDto save(RoleDto roleDto){
        Role role = roleFromDto(roleDto);
        Role savedRole = roleRepository.save(role);
        return roleToDto(savedRole);
    }

    public void remove(Long id){
        roleRepository.deleteById(id);
    }

    public boolean existsById(Long id){
        return roleRepository.existsById(id);
    }

    public Optional<RoleDto> findRoleById(Long id){
        return roleRepository.findRoleById(id)
                .map(this::roleToDto);
    }

    public RoleDto roleToDto(Role role){
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    public Role roleFromDto(RoleDto roleDto){
        return Role.builder()
                .id(roleDto.getId())
                .name(roleDto.getName())
                .build();
    }
}
