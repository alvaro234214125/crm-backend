package com.dev.backend_crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuotationStatsDto {
    private long total;
    private long accepted;
    private long rejected;
}