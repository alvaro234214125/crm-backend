package com.dev.backend_crm.service.impl;

import com.dev.backend_crm.dto.QuotationDetailDto;
import com.dev.backend_crm.dto.QuotationDto;
import com.dev.backend_crm.dto.QuotationStatsDto;
import com.dev.backend_crm.entity.*;
import com.dev.backend_crm.repository.ClientRepository;
import com.dev.backend_crm.repository.ProductRepository;
import com.dev.backend_crm.repository.QuotationDetailRepository;
import com.dev.backend_crm.repository.QuotationRepository;
import com.dev.backend_crm.service.ActivityLogService;
import com.dev.backend_crm.service.QuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuotationServiceImpl implements QuotationService {

    private final QuotationRepository quotationRepository;
    private final QuotationDetailRepository quotationDetailRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final ActivityLogService activityLogService;

    @Override
    public QuotationDto create(QuotationDto dto, String performedBy) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Quotation quotation = Quotation.builder()
                .client(client)
                .issueDate(dto.getIssueDate())
                .status(dto.getStatus())
                .total(dto.getTotal())
                .build();

        Quotation saved = quotationRepository.save(quotation);

        List<QuotationDetail> details = dto.getDetails().stream().map(d -> {
            Product product = productRepository.findById(d.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            return QuotationDetail.builder()
                    .quotation(saved)
                    .product(product)
                    .quantity(d.getQuantity())
                    .unitPrice(d.getUnitPrice())
                    .build();
        }).toList();

        quotationDetailRepository.saveAll(details);

        activityLogService.log("CREATE_QUOTATION", "Creó cotización para cliente ID: " + client.getId(), "Quotation", saved.getId(), performedBy);

        return getById(saved.getId());
    }

    @Override
    public QuotationDto getById(Long id) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quotation not found"));

        List<QuotationDetail> details = quotationDetailRepository.findByQuotationId(id);

        return QuotationDto.builder()
                .id(quotation.getId())
                .clientId(quotation.getClient().getId())
                .issueDate(quotation.getIssueDate())
                .status(quotation.getStatus())
                .total(quotation.getTotal())
                .details(details.stream().map(d -> QuotationDetailDto.builder()
                        .productId(d.getProduct().getId())
                        .quantity(d.getQuantity())
                        .unitPrice(d.getUnitPrice())
                        .build()).toList())
                .build();
    }

    @Override
    public PageResponse<QuotationDto> getAll(Pageable pageable) {
        Page<Quotation> page = quotationRepository.findAll(pageable);
        List<QuotationDto> content = page.getContent().stream().map(q -> getById(q.getId())).toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Override
    public void delete(Long id, String performedBy) {
        if (!quotationRepository.existsById(id))
            throw new RuntimeException("Quotation not found");

        quotationRepository.deleteById(id);
        activityLogService.log("DELETE_QUOTATION", "Eliminó la cotización con ID: " + id, "Quotation", id, performedBy);
    }

    @Override
    public QuotationStatsDto getStats() {
        long total = quotationRepository.count();
        long accepted = quotationRepository.countByStatus(QuotationStatus.Accepted);
        long rejected = quotationRepository.countByStatus(QuotationStatus.Rejected);
        return new QuotationStatsDto(total, accepted, rejected);
    }
}