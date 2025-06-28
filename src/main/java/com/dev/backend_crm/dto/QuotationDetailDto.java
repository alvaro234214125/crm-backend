package com.dev.backend_crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuotationDetailDto {
    private Long productId;
    private int quantity;
    private Double unitPrice;
}