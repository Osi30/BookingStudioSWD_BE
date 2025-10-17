package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.PriceRuleRequest;
import com.studio.booking.dtos.response.PriceRuleResponse;
import com.studio.booking.entities.PriceRule;
import com.studio.booking.entities.PriceTableItem;
import com.studio.booking.repositories.PriceRuleRepo;
import com.studio.booking.repositories.PriceTableItemRepo;
import com.studio.booking.services.PriceRuleService;
import com.studio.booking.utils.BitUtil;
import com.studio.booking.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.studio.booking.exceptions.exceptions.AccountException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceRuleServiceImpl implements PriceRuleService {
    private final PriceRuleRepo ruleRepo;
    private final PriceTableItemRepo itemRepo;

    @Override
    public List<PriceRuleResponse> getByItemId(String priceTableItemId) {
        return ruleRepo.findAllByPriceTableItem_IdAndIsDeletedFalse(priceTableItemId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PriceRuleResponse create(PriceRuleRequest req) {
        PriceTableItem item = itemRepo.findById(req.getPriceTableItemId())
                .orElseThrow(() -> new AccountException("PriceTableItem not found with id: " + req.getPriceTableItemId()));

        int dayFilter = req.getDaysOfWeek()
                .stream().mapToInt(BitUtil::calculateDayBit)
                .sum();

        PriceRule rule = PriceRule.builder()
                .priceTableItem(item)
                .dayFilter(dayFilter)
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
    public PriceRuleResponse update(String id, PriceRuleRequest req) {
        PriceRule rule = ruleRepo.findById(id)
                .orElseThrow(() -> new AccountException("PriceRule not found with id: " + id));

        if (Validation.isValidCollection(req.getDaysOfWeek())) {
            int dayFilter = req.getDaysOfWeek()
                    .stream().mapToInt(BitUtil::calculateDayBit)
                    .sum();
            rule.setDayFilter(dayFilter);
        }
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

    private PriceRuleResponse toResponse(PriceRule rule) {
        return PriceRuleResponse.builder()
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
