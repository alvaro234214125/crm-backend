package com.dev.backend_crm.service.impl;

import com.dev.backend_crm.dto.PaymentDto;
import com.dev.backend_crm.dto.PaymentStatsDto;
import com.dev.backend_crm.entity.Invoice;
import com.dev.backend_crm.entity.PageResponse;
import com.dev.backend_crm.entity.Payment;
import com.dev.backend_crm.entity.PaymentMethod;
import com.dev.backend_crm.repository.InvoiceRepository;
import com.dev.backend_crm.repository.PaymentRepository;
import com.dev.backend_crm.service.ActivityLogService;
import com.dev.backend_crm.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final ActivityLogService activityLogService;

    @Override
    public PaymentDto create(PaymentDto dto, String performedBy) {
        Invoice invoice = invoiceRepository.findById(dto.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        Payment payment = Payment.builder()
                .invoice(invoice)
                .amount(dto.getAmount())
                .paymentDate(dto.getPaymentDate())
                .paymentMethod(dto.getPaymentMethod())
                .build();

        Payment saved = paymentRepository.save(payment);
        activityLogService.log("CREATE_PAYMENT", "Registró pago de: S/" + dto.getAmount(), "Payment", saved.getId(), performedBy);

        return toDto(saved);
    }

    @Override
    public PaymentDto getById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return toDto(payment);
    }

    @Override
    public PageResponse<PaymentDto> getAll(Pageable pageable) {
        Page<Payment> page = paymentRepository.findAll(pageable);
        List<PaymentDto> content = page.getContent().stream().map(this::toDto).toList();
        return new PageResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Override
    public void delete(Long id, String performedBy) {
        if (!paymentRepository.existsById(id))
            throw new RuntimeException("Payment not found");

        paymentRepository.deleteById(id);
        activityLogService.log("DELETE_PAYMENT", "Eliminó el pago con ID: " + id, "Payment", id, performedBy);
    }

    @Override
    public PaymentStatsDto getStats() {
        long total = paymentRepository.count();
        double amount = paymentRepository.findAll().stream().mapToDouble(Payment::getAmount).sum();
        long byCash = paymentRepository.countByPaymentMethod(PaymentMethod.Cash);
        long byCard = paymentRepository.countByPaymentMethod(PaymentMethod.Card);
        long byTransfer = paymentRepository.countByPaymentMethod(PaymentMethod.Transfer);
        long byBank = paymentRepository.countByPaymentMethod(PaymentMethod.Bank);

        return new PaymentStatsDto(total, amount, byCash, byCard, byTransfer, byBank);
    }

    private PaymentDto toDto(Payment p) {
        return PaymentDto.builder()
                .id(p.getId())
                .invoiceId(p.getInvoice().getId())
                .amount(p.getAmount())
                .paymentDate(p.getPaymentDate())
                .paymentMethod(p.getPaymentMethod())
                .build();
    }
}