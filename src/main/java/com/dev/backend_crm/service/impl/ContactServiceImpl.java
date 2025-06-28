package com.dev.backend_crm.service.impl;

import com.dev.backend_crm.dto.ContactDto;
import com.dev.backend_crm.dto.ContactStatsDto;
import com.dev.backend_crm.entity.Client;
import com.dev.backend_crm.entity.Contact;
import com.dev.backend_crm.repository.ClientRepository;
import com.dev.backend_crm.repository.ContactRepository;
import com.dev.backend_crm.repository.UserRepository;
import com.dev.backend_crm.service.ActivityLogService;
import com.dev.backend_crm.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {
    private final ContactRepository contactRepository;
    private final ClientRepository clientRepository;
    private final ActivityLogService activityLogService;
    private final UserRepository userRepository;

    @Override
    public List<ContactDto> getByClientId(Long clientId) {
        return contactRepository.findByClientId(clientId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public ContactDto getById(Long id) {
        Contact c = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
        return toDto(c);
    }

    @Override
    public ContactDto create(ContactDto dto, String performedBy) {
        Contact c = fromDto(dto);
        Contact saved = contactRepository.save(c);

        activityLogService.log("CREATE_CONTACT", "Creó al contacto: " + saved.getName(), "Contact", saved.getId(), performedBy);
        return toDto(saved);
    }

    @Override
    public ContactDto update(Long id, ContactDto dto, String performedBy) {
        Contact existing = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setPosition(dto.getPosition());

        Contact updated = contactRepository.save(existing);

        activityLogService.log("UPDATE_CONTACT", "Actualizó al contacto: " + updated.getName(), "Contact", updated.getId(), performedBy);
        return toDto(updated);
    }

    @Override
    public void delete(Long id, String performedBy) {
        if (!contactRepository.existsById(id)) {
            throw new RuntimeException("Contact not found");
        }

        contactRepository.deleteById(id);
        activityLogService.log("DELETE_CONTACT", "Eliminó al contacto con ID: " + id, "Contact", id, performedBy);
    }

    private ContactDto toDto(Contact c) {
        return ContactDto.builder()
                .id(c.getId())
                .clientId(c.getClient().getId())
                .name(c.getName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .position(c.getPosition())
                .build();
    }

    private Contact fromDto(ContactDto dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        return Contact.builder()
                .id(dto.getId())
                .client(client)
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .position(dto.getPosition())
                .build();
    }

    @Override
    public ContactStatsDto getContactStats() {
        long total = contactRepository.count();
        long porCliente = contactRepository.findAll()
                .stream()
                .map(Contact::getClient)
                .distinct()
                .count();

        return new ContactStatsDto(total, porCliente);
    }

}