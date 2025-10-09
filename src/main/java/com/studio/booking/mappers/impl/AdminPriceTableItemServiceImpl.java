package com.studio.booking.mappers.impl;

import com.studio.booking.dtos.request.AdminPriceTableItemRequest;
import com.studio.booking.dtos.response.AdminPriceTableItemResponse;
import com.studio.booking.entities.PriceTable;
import com.studio.booking.entities.PriceTableItem;
import com.studio.booking.entities.StudioType;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.repositories.PriceTableItemRepo;
import com.studio.booking.repositories.PriceTableRepo;
import com.studio.booking.repositories.StudioTypeRepo;
import com.studio.booking.services.AdminPriceTableItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPriceTableItemServiceImpl  implements AdminPriceTableItemService {
    private final PriceTableItemRepo itemRepo;
    private final PriceTableRepo tableRepo;
    private final StudioTypeRepo studioTypeRepo;

    @Override
    public List<AdminPriceTableItemResponse> getByTableId(String priceTableId) {
        return itemRepo.findAllByPriceTable_Id(priceTableId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AdminPriceTableItemResponse create(AdminPriceTableItemRequest req) {
        PriceTable table = tableRepo.findById(req.getPriceTableId())
                .orElseThrow(() -> new AccountException("PriceTable not found with id: " + req.getPriceTableId()));

        StudioType type = studioTypeRepo.findById(req.getStudioTypeId())
                .orElseThrow(() -> new AccountException("StudioType not found with id: " + req.getStudioTypeId()));

        PriceTableItem item = PriceTableItem.builder()
                .priceTable(table)
                .studioType(type)
                .defaultPrice(req.getDefaultPrice())
                .build();

        itemRepo.save(item);
        return toResponse(item);
    }

    @Override
    public AdminPriceTableItemResponse update(String id, AdminPriceTableItemRequest req) {
        PriceTableItem item = itemRepo.findById(id)
                .orElseThrow(() -> new AccountException("PriceTableItem not found with id: " + id));

        if (req.getDefaultPrice() != null) {
            item.setDefaultPrice(req.getDefaultPrice());
        }

        if (req.getStudioTypeId() != null) {
            StudioType type = studioTypeRepo.findById(req.getStudioTypeId())
                    .orElseThrow(() -> new AccountException("StudioType not found with id: " + req.getStudioTypeId()));
            item.setStudioType(type);
        }

        if (req.getPriceTableId() != null) {
            PriceTable table = tableRepo.findById(req.getPriceTableId())
                    .orElseThrow(() -> new AccountException("PriceTable not found with id: " + req.getPriceTableId()));
            item.setPriceTable(table);
        }

        itemRepo.save(item);
        return toResponse(item);
    }

    @Override
    public String delete(String id) {
        PriceTableItem item = itemRepo.findById(id)
                .orElseThrow(() -> new AccountException("PriceTableItem not found with id: " + id));
        itemRepo.delete(item);
        return "PriceTableItem deleted successfully!";
    }

    private AdminPriceTableItemResponse toResponse(PriceTableItem item) {
        return AdminPriceTableItemResponse.builder()
                .id(item.getId())
                .priceTableId(item.getPriceTable() != null ? item.getPriceTable().getId() : null)
                .studioTypeName(item.getStudioType() != null ? item.getStudioType().getName() : null)
                .defaultPrice(item.getDefaultPrice())
                .build();
    }
}
