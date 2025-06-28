package com.dev.backend_crm.service.impl;

import com.dev.backend_crm.dto.InvoiceDetailDto;
import com.dev.backend_crm.dto.InvoiceDto;
import com.dev.backend_crm.dto.InvoiceStatsDto;
import com.dev.backend_crm.entity.*;
import com.dev.backend_crm.repository.ClientRepository;
import com.dev.backend_crm.repository.InvoiceDetailRepository;
import com.dev.backend_crm.repository.InvoiceRepository;
import com.dev.backend_crm.repository.ProductRepository;
import com.dev.backend_crm.service.ActivityLogService;
import com.dev.backend_crm.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final ActivityLogService activityLogService;

    @Override
    public InvoiceDto create(InvoiceDto dto, String performedBy) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Invoice invoice = Invoice.builder()
                .client(client)
                .issueDate(dto.getIssueDate())
                .total(dto.getTotal())
                .build();

        Invoice saved = invoiceRepository.save(invoice);

        List<InvoiceDetail> details = dto.getDetails().stream().map(d -> {
            Product product = productRepository.findById(d.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            return InvoiceDetail.builder()
                    .invoice(saved)
                    .product(product)
                    .quantity(d.getQuantity())
                    .unitPrice(d.getUnitPrice())
                    .build();
        }).toList();

        invoiceDetailRepository.saveAll(details);

        activityLogService.log("CREATE_INVOICE", "Creó factura para cliente ID: " + client.getId(), "Invoice", saved.getId(), performedBy);

        return getById(saved.getId());
    }

    @Override
    public InvoiceDto getById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        List<InvoiceDetail> details = invoiceDetailRepository.findByInvoiceId(id);

        return InvoiceDto.builder()
                .id(invoice.getId())
                .clientId(invoice.getClient().getId())
                .issueDate(invoice.getIssueDate())
                .total(invoice.getTotal())
                .details(details.stream().map(d -> InvoiceDetailDto.builder()
                        .productId(d.getProduct().getId())
                        .quantity(d.getQuantity())
                        .unitPrice(d.getUnitPrice())
                        .build()).toList())
                .build();
    }

    @Override
    public PageResponse<InvoiceDto> getAll(Pageable pageable) {
        Page<Invoice> page = invoiceRepository.findAll(pageable);
        List<InvoiceDto> content = page.getContent().stream().map(i -> getById(i.getId())).toList();
        return new PageResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Override
    public void delete(Long id, String performedBy) {
        if (!invoiceRepository.existsById(id))
            throw new RuntimeException("Invoice not found");

        invoiceRepository.deleteById(id);
        activityLogService.log("DELETE_INVOICE", "Eliminó la factura con ID: " + id, "Invoice", id, performedBy);
    }

    @Override
    public InvoiceStatsDto getStats() {
        long total = invoiceRepository.count();
        double revenue = invoiceRepository.getTotalRevenue() != null ? invoiceRepository.getTotalRevenue() : 0;
        long clients = invoiceRepository.getDistinctClientCount();
        return new InvoiceStatsDto(total, revenue, clients);
    }
}
