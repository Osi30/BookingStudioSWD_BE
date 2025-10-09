package com.studio.booking.mappers.impl;

import com.studio.booking.dtos.request.AdminPriceRuleRequest;
import com.studio.booking.dtos.response.AdminPriceRuleResponse;
import com.studio.booking.entities.PriceRule;
import com.studio.booking.entities.PriceTableItem;
import com.studio.booking.repositories.PriceRuleRepo;
import com.studio.booking.repositories.PriceTableItemRepo;
import com.studio.booking.services.AdminPriceRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.studio.booking.exceptions.exceptions.AccountException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPriceRuleServiceImpl implements AdminPriceRuleService {
    private final PriceRuleRepo ruleRepo;
    private final PriceTableItemRepo itemRepo;

    @Override
    public List<AdminPriceRuleResponse> getByItemId(String priceTableItemId) {
        return ruleRepo.findAllByPriceTableItem_IdAndIsDeletedFalse(priceTableItemId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AdminPriceRuleResponse create(AdminPriceRuleRequest req) {
        PriceTableItem item = itemRepo.findById(req.getPriceTableItemId())
                .orElseThrow(() -> new AccountException("PriceTableItem not found with id: " + req.getPriceTableItemId()));

        PriceRule rule = PriceRule.builder()
                .priceTableItem(item)
                .dayFilter(req.getDayFilter())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .pricePerUnit(req.getPricePerUnit())
                .unit(req.getUnit())
                .date(req.getDate())
                .isDeleted(false)
                .build();

        ruleRepo.save(rule);
        return toResponse(rule);
    }

    @Override
    public AdminPriceRuleResponse update(String id, AdminPriceRuleRequest req) {
        PriceRule rule = ruleRepo.findById(id)
                .orElseThrow(() -> new AccountException("PriceRule not found with id: " + id));

        if (req.getDayFilter() != null) rule.setDayFilter(req.getDayFilter());
        if (req.getStartTime() != null) rule.setStartTime(req.getStartTime());
        if (req.getEndTime() != null) rule.setEndTime(req.getEndTime());
        if (req.getPricePerUnit() != null) rule.setPricePerUnit(req.getPricePerUnit());
        if (req.getUnit() != null) rule.setUnit(req.getUnit());
        if (req.getDate() != null) rule.setDate(req.getDate());

        ruleRepo.save(rule);
        return toResponse(rule);
    }

    @Override
    public String delete(String id) {
        PriceRule rule = ruleRepo.findById(id)
                .orElseThrow(() -> new AccountException("PriceRule not found with id: " + id));
        rule.setIsDeleted(true);
        ruleRepo.save(rule);
        return "PriceRule deleted successfully!";
    }

    private AdminPriceRuleResponse toResponse(PriceRule rule) {
        return AdminPriceRuleResponse.builder()
                .id(rule.getId())
                .priceTableItemId(rule.getPriceTableItem() != null ? rule.getPriceTableItem().getId() : null)
                .dayFilter(rule.getDayFilter())
                .startTime(rule.getStartTime())
                .endTime(rule.getEndTime())
                .pricePerUnit(rule.getPricePerUnit())
                .unit(rule.getUnit())
                .date(rule.getDate())
                .isDeleted(rule.getIsDeleted())
                .build();
    }
}
