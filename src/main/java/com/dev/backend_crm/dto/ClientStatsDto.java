package com.dev.backend_crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientStatsDto {
    private long total;
    private long activos;
    private long inactivos;
}
