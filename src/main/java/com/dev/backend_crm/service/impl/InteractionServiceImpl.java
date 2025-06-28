package com.dev.backend_crm.service.impl;

import com.dev.backend_crm.dto.InteractionDto;
import com.dev.backend_crm.dto.InteractionStatsDto;
import com.dev.backend_crm.entity.Client;
import com.dev.backend_crm.entity.Interaction;
import com.dev.backend_crm.entity.PageResponse;
import com.dev.backend_crm.entity.User;
import com.dev.backend_crm.repository.ClientRepository;
import com.dev.backend_crm.repository.InteractionRepository;
import com.dev.backend_crm.repository.UserRepository;
import com.dev.backend_crm.service.ActivityLogService;
import com.dev.backend_crm.service.InteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InteractionServiceImpl implements InteractionService {

    private final InteractionRepository interactionRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    @Override
    public PageResponse<InteractionDto> getAll(Pageable pageable) {
        Page<Interaction> page = interactionRepository.findAll(pageable);
        List<InteractionDto> content = page.getContent().stream().map(this::toDto).collect(Collectors.toList());

        return new PageResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Override
    public InteractionDto getById(Long id) {
        Interaction i = interactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Interaction not found"));
        return toDto(i);
    }

    @Override
    public InteractionDto create(InteractionDto dto, String performedBy) {
        Interaction i = fromDto(dto);
        i.setDate(new Date());
        Interaction saved = interactionRepository.save(i);
        activityLogService.log("CREATE_INTERACTION", "Registró una interacción", "Interaction", saved.getId(), performedBy);
        return toDto(saved);
    }

    @Override
    public InteractionDto update(Long id, InteractionDto dto, String performedBy) {
        Interaction existing = interactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Interaction not found"));

        existing.setType(dto.getType());
        existing.setDescription(dto.getDescription());
        existing.setDate(dto.getDate());

        Interaction updated = interactionRepository.save(existing);
        activityLogService.log("UPDATE_INTERACTION", "Actualizó una interacción", "Interaction", updated.getId(), performedBy);
        return toDto(updated);
    }

    @Override
    public void delete(Long id, String performedBy) {
        if (!interactionRepository.existsById(id))
            throw new RuntimeException("Interaction not found");
        interactionRepository.deleteById(id);
        activityLogService.log("DELETE_INTERACTION", "Eliminó una interacción", "Interaction", id, performedBy);
    }

    @Override
    public InteractionStatsDto getStats() {
        long total = interactionRepository.count();

        long tiposUnicos = interactionRepository.findAll()
                .stream().map(Interaction::getType).distinct().count();

        long clientesConInteraccion = interactionRepository.findAll()
                .stream().map(i -> i.getClient().getId()).distinct().count();

        return new InteractionStatsDto(total, tiposUnicos, clientesConInteraccion);
    }

    private InteractionDto toDto(Interaction i) {
        return InteractionDto.builder()
                .id(i.getId())
                .clientId(i.getClient().getId())
                .userId(i.getUser().getId())
                .type(i.getType())
                .description(i.getDescription())
                .date(i.getDate())
                .build();
    }

    private Interaction fromDto(InteractionDto dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return Interaction.builder()
                .id(dto.getId())
                .client(client)
                .user(user)
                .type(dto.getType())
                .description(dto.getDescription())
                .date(dto.getDate())
                .build();
    }
}
