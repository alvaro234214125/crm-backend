package com.dev.backend_crm.dto;

import com.dev.backend_crm.entity.QuotationStatus;
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
public class QuotationDto {
    private Long id;
    private Long clientId;
    private Date issueDate;
    private QuotationStatus status;
    private Double total;
    private List<QuotationDetailDto> details;
}