package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.PriceTableRequest;
import com.studio.booking.dtos.response.PriceTableResponse;
import com.studio.booking.entities.PriceTable;
import com.studio.booking.enums.PriceTableStatus;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.repositories.PriceTableRepo;
import com.studio.booking.services.PriceTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceTableServiceImpl implements PriceTableService {
    private final PriceTableRepo priceTableRepo;

    @Override
    public List<PriceTableResponse> getAll() {
        return priceTableRepo.findAllByStatusNot(PriceTableStatus.DELETED)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PriceTableResponse getById(String id) {
        PriceTable table = priceTableRepo.findById(id)
                .orElseThrow(() -> new AccountException("PriceTable not found with id: " + id));
        return toResponse(table);
    }

    @Override
    public PriceTableResponse create(PriceTableRequest req) {
        PriceTable table = PriceTable.builder()
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .priority(req.getPriority())
                .status(req.getStatus() != null ? req.getStatus() : PriceTableStatus.COMING_SOON)
                .build();
        priceTableRepo.save(table);
        return toResponse(table);
    }

    @Override
    public PriceTableResponse update(String id, PriceTableRequest req) {
        PriceTable table = priceTableRepo.findById(id)
                .orElseThrow(() -> new AccountException("PriceTable not found with id: " + id));

        if (req.getStartDate() != null) table.setStartDate(req.getStartDate());
        if (req.getEndDate() != null) table.setEndDate(req.getEndDate());
        if (req.getPriority() != null) table.setPriority(req.getPriority());
        if (req.getStatus() != null) table.setStatus(req.getStatus());

        priceTableRepo.save(table);
        return toResponse(table);
    }

    @Override
    public String delete(String id) {
        PriceTable table = priceTableRepo.findById(id)
                .orElseThrow(() -> new AccountException("PriceTable not found with id: " + id));
        table.setStatus(PriceTableStatus.DELETED);
        priceTableRepo.save(table);
        return "PriceTable deleted successfully!";
    }

    private PriceTableResponse toResponse(PriceTable table) {
        return PriceTableResponse.builder()
                .id(table.getId())
                .startDate(table.getStartDate())
                .endDate(table.getEndDate())
                .priority(table.getPriority())
                .status(table.getStatus())
                .build();
    }
}
