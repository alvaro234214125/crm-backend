package com.dev.backend_crm.service.impl;

import com.dev.backend_crm.dto.ClientDto;
import com.dev.backend_crm.dto.ClientStatsDto;
import com.dev.backend_crm.entity.Client;
import com.dev.backend_crm.entity.PageResponse;
import com.dev.backend_crm.entity.User;
import com.dev.backend_crm.repository.ClientRepository;
import com.dev.backend_crm.repository.UserRepository;
import com.dev.backend_crm.service.ActivityLogService;
import com.dev.backend_crm.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    @Override
    public PageResponse<ClientDto> getAllClients(Pageable pageable) {
        Page<Client> page = clientRepository.findAll(pageable);

        List<ClientDto> content = page.getContent()
                .stream()
                .map(this::toDto)
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

    @Override
    public ClientDto getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        return toDto(client);
    }

    @Override
    public ClientDto create(ClientDto dto, String performedBy) {
        Client client = fromDto(dto);
        client.setRegisteredAt(new Date());
        Client saved = clientRepository.save(client);

        activityLogService.log("CREATE_CLIENT", "Creó al cliente: " + saved.getName(), "Client", saved.getId(), performedBy);
        return toDto(saved);
    }

    @Override
    public ClientDto update(Long id, ClientDto dto, String performedBy) {
        Client existing = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        existing.setName(dto.getName());
        existing.setType(dto.getType());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setAddress(dto.getAddress());
        existing.setStatus(dto.getStatus());

        User assignedUser = userRepository.findById(dto.getAssignedUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        existing.setAssignedUser(assignedUser);

        Client updated = clientRepository.save(existing);

        activityLogService.log("UPDATE_CLIENT", "Actualizó al cliente: " + updated.getName(), "Client", updated.getId(), performedBy);
        return toDto(updated);
    }

    @Override
    public void delete(Long id, String performedBy) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Client not found");
        }

        clientRepository.deleteById(id);
        activityLogService.log("DELETE_CLIENT", "Eliminó al cliente con ID: " + id, "Client", id, performedBy);
    }

    @Override
    public ClientDto toDto(Client client) {
        return ClientDto.builder()
                .id(client.getId())
                .name(client.getName())
                .type(client.getType())
                .email(client.getEmail())
                .phone(client.getPhone())
                .address(client.getAddress())
                .assignedUserId(client.getAssignedUser().getId())
                .status(client.getStatus())
                .registeredAt(client.getRegisteredAt())
                .build();
    }

    @Override
    public Client fromDto(ClientDto dto) {
        User assignedUser = userRepository.findById(dto.getAssignedUserId())
                .orElseThrow(() -> new RuntimeException("Assigned user not found"));

        return Client.builder()
                .id(dto.getId())
                .name(dto.getName())
                .type(dto.getType())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .assignedUser(assignedUser)
                .status(dto.getStatus())
                .registeredAt(dto.getRegisteredAt())
                .build();
    }

    public PageResponse<ClientDto> searchClients(String name, String email, String type, String status, Pageable pageable) {
        Specification<Client> spec = (root, query, cb) -> cb.conjunction();

        if (name != null && !name.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (email != null && !email.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
        }

        if (type != null && !type.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }

        if (status != null && !status.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        Page<Client> page = clientRepository.findAll(spec, pageable);
        List<ClientDto> content = page.getContent().stream().map(this::toDto).collect(Collectors.toList());

        return new PageResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    public ClientStatsDto getClientStats() {
        long total = clientRepository.count();
        long activos = clientRepository.countByStatus("activo");
        long inactivos = clientRepository.countByStatus("inactivo");
        return new ClientStatsDto(total, activos, inactivos);
    }

}
