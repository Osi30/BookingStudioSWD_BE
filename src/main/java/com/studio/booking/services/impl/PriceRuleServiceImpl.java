package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.PriceRuleRequest;
import com.studio.booking.dtos.response.PriceRuleResponse;
import com.studio.booking.entities.PriceRule;
import com.studio.booking.entities.PriceTableItem;
import com.studio.booking.enums.PriceUnit;
import com.studio.booking.exceptions.exceptions.PriceTableException;
import com.studio.booking.repositories.PriceRuleRepo;
import com.studio.booking.repositories.PriceTableItemRepo;
import com.studio.booking.services.PriceRuleService;
import com.studio.booking.utils.BitUtil;
import com.studio.booking.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import com.studio.booking.exceptions.exceptions.AccountException;

import java.time.DayOfWeek;
import java.util.Arrays;
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
    public List<PriceRuleResponse> getByTableAndType(String tableId, String typeId) {
        return ruleRepo.findAllByTableAndStudioType(tableId, typeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PriceRuleResponse create(PriceRuleRequest req) {
        PriceTableItem item = itemRepo.findById(req.getPriceTableItemId())
                .orElseThrow(() -> new AccountException("PriceTableItem not found with id: " + req.getPriceTableItemId()));

        Integer dayFilter = Validation.isValidCollection(req.getDaysOfWeek())
                ? req.getDaysOfWeek().stream().mapToInt(BitUtil::calculateDayBit).sum()
                : null;

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

        validateRule(rule);

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

        validateRule(rule);

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
                .dayFilter(Arrays.stream(DayOfWeek.values())
                        .filter(dow -> rule.getDayFilter() != null
                                && (rule.getDayFilter() & BitUtil.calculateDayBit(dow)) > 0)
                        .toList())
                .startTime(rule.getStartTime())
                .endTime(rule.getEndTime())
                .pricePerUnit(rule.getPricePerUnit())
                .unit(rule.getUnit())
                .date(rule.getDate())
                .isDeleted(rule.getIsDeleted())
                .build();
    }

    private void validateRule(PriceRule rule) {
        // Case for DayOfWeek
        if (rule.getDayFilter() != null && rule.getDate() != null) {
            throw new PriceTableException("It is not possible to have the same rule on day and date");
        }

        // Case for Time Interval
        if (rule.getStartTime() != null && rule.getEndTime() != null
                && !rule.getStartTime().isBefore(rule.getEndTime())) {
            throw new PriceTableException("Start Time cannot be after End Time");
        }

        if (rule.getStartTime() == null && rule.getEndTime() != null) {
            throw new PriceTableException("Conflict Time Interval: Start time is null");
        }

        if (rule.getStartTime() != null && rule.getEndTime() == null) {
            throw new PriceTableException("Conflict Time Interval: End time is null");
        }

        // Case for Unit
        if (!rule.getUnit().equals(PriceUnit.HOUR)){
            throw new PriceTableException("Unsupported unit: " + rule.getUnit());
        }

        // Case for price
        if (rule.getPricePerUnit() <= 0){
            throw new PriceTableException("Price per unit must be greater than 0");
        }
    }
}
