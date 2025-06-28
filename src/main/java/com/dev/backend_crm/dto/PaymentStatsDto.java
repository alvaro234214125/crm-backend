package com.dev.backend_crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatsDto {
    private long totalPayments;
    private double totalAmount;
    private long byCash;
    private long byCard;
    private long byTransfer;
    private long byBank;
}