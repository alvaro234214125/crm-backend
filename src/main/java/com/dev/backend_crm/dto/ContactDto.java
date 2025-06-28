package com.dev.backend_crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactDto {
    private Long id;
    private Long clientId;
    private String name;
    private String email;
    private String phone;
    private String position;
}
