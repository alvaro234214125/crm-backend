package com.dev.backend_crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductStatsDto {
    private long total;
    private long tiposUnicos;
    private double precioPromedio;
}
