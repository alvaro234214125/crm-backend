package com.dev.backend_crm.dto;

import com.dev.backend_crm.entity.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long id;
    private Long invoiceId;
    private Double amount;
    private Date paymentDate;
    private PaymentMethod paymentMethod;
}