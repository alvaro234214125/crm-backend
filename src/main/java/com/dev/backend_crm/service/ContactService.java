package com.dev.backend_crm.service;

import com.dev.backend_crm.dto.ContactDto;
import com.dev.backend_crm.dto.ContactStatsDto;

import java.util.List;

public interface ContactService {
    List<ContactDto> getByClientId(Long clientId);
    ContactDto getById(Long id);
    ContactDto create(ContactDto dto, String performedBy);
    ContactDto update(Long id, ContactDto dto, String performedBy);
    void delete(Long id, String performedBy);
    ContactStatsDto getContactStats();
}