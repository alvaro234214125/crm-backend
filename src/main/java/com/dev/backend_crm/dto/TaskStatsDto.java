package com.dev.backend_crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskStatsDto {
    private long total;
    private long completadas;
    private long pendientes;
}