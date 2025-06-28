package com.dev.backend_crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InteractionDto {
    private Long id;
    private Long clientId;
    private Long userId;
    private String type;
    private String description;
    private Date date;
}