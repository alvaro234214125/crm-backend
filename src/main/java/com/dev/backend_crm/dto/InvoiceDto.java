package com.dev.backend_crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDto {
    private Long id;
    private Long clientId;
    private Date issueDate;
    private Double total;
    private List<InvoiceDetailDto> details;
}

