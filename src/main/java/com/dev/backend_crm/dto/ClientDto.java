package com.dev.backend_crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientDto {
    private Long id;
    private String type;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Long assignedUserId;
    private String status;
    private Date registeredAt;
}
