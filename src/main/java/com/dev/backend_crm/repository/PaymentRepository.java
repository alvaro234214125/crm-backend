package com.dev.backend_crm.repository;

import com.dev.backend_crm.entity.Payment;
import com.dev.backend_crm.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    long countByPaymentMethod(PaymentMethod method);
    double countByInvoiceId(Long invoiceId);
}