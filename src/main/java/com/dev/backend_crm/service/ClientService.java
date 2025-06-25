package com.dev.backend_crm.service;

import com.dev.backend_crm.dto.ClientDto;
import com.dev.backend_crm.entity.Client;
import com.dev.backend_crm.entity.PageResponse;
import org.springframework.data.domain.Pageable;
import com.dev.backend_crm.dto.ClientStatsDto;
import org.springframework.data.domain.Pageable;
import com.dev.backend_crm.entity.PageResponse;

public interface ClientService {
    PageResponse<ClientDto> getAllClients(Pageable pageable);
    ClientDto getClientById(Long id);
    ClientDto create(ClientDto dto, String performedBy);
    ClientDto update(Long id, ClientDto dto, String performedBy);
    void delete(Long id, String performedBy);
    ClientDto toDto(Client client);
    Client fromDto(ClientDto dto);
    PageResponse<ClientDto> searchClients(String name, String email, String type, String status, Pageable pageable);
    ClientStatsDto getClientStats();
}
