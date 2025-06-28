package com.dev.backend_crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceStatsDto {
    private long total;
    private double totalRevenue;
    private long clientsWithInvoices;
}
