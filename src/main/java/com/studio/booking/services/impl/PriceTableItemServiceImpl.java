package com.studio.booking.services.impl;

import com.studio.booking.dtos.dto.PriceResultDTO;
import com.studio.booking.dtos.request.PriceTableItemRequest;
import com.studio.booking.dtos.response.PriceResultResponse;
import com.studio.booking.dtos.response.PriceTableItemResponse;
import com.studio.booking.entities.PriceRule;
import com.studio.booking.entities.PriceTable;
import com.studio.booking.entities.PriceTableItem;
import com.studio.booking.entities.StudioType;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.exceptions.exceptions.PriceTableException;
import com.studio.booking.repositories.PriceTableItemRepo;
import com.studio.booking.repositories.PriceTableRepo;
import com.studio.booking.repositories.StudioTypeRepo;
import com.studio.booking.services.PriceTableItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceTableItemServiceImpl implements PriceTableItemService {
    private final PriceTableItemRepo itemRepo;
    private final PriceTableRepo tableRepo;
    private final StudioTypeRepo studioTypeRepo;

    @Override
    public List<PriceTableItemResponse> getByTableId(String priceTableId) {
        return itemRepo.findAllByPriceTable_Id(priceTableId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PriceTableItemResponse create(PriceTableItemRequest req) {
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
    public PriceTableItemResponse update(String id, PriceTableItemRequest req) {
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

    @Override
    public PriceResultResponse getPriceByTypeAndTime(String studioTypeId, LocalDateTime startTime, LocalDateTime endTime) {
        PriceTableItem priceItem = itemRepo.findFirstByStudioTypeAndDate(
                studioTypeId, startTime.toLocalDate()
        );

        if (priceItem == null) {
            throw new PriceTableException("There are no price items for this studio type");
        }

        int dayBit = calculateDateBit(startTime.toLocalDate());

        List<PriceRule> allRules = priceItem.getRules();
        List<PriceRule> applicableRules = allRules.stream()
                .filter(r -> {
                    if (Boolean.TRUE.equals(r.getIsDeleted())) {
                        return false;
                    }

                    // Price Rule by Time
                    if (r.getStartTime() != null && r.getEndTime() != null) {
                        return (r.getStartTime().isBefore(endTime.toLocalTime())
                                && r.getEndTime().isAfter(startTime.toLocalTime()));
                    }

                    // Price Rule by Date
                    if (r.getDate() != null) {
                        return r.getDate().equals(startTime.toLocalDate());
                    }

                    // Price Rule by DayOfWeek
                    return isDayRuleApplicable(r, dayBit);
                })
                .toList();

        List<PriceResultDTO> priceResults = new ArrayList<>();
        Double totalPrice = 0d;
        for (LocalTime beginTime = startTime.toLocalTime(); beginTime.isBefore(endTime.toLocalTime()); beginTime = beginTime.plusHours(1)) {
            PriceRule priceRule = getRuleForTimeInterval(applicableRules, beginTime, beginTime.plusHours(1));
            if (priceRule == null) {
                priceRule = new PriceRule();
                priceRule.setPricePerUnit(priceItem.getDefaultPrice());
            }

            // Update if last result has same price rule
            PriceResultDTO priceResultDTO = priceResults.getLast();
            if (priceResultDTO != null) {
                priceResultDTO.setEndTime(beginTime.plusHours(1));
            } else {
                priceResults.add(PriceResultDTO.builder()
                        .startTime(beginTime)
                        .endTime(beginTime.plusHours(1))
                        .price(priceRule.getPricePerUnit())
                        .build());
            }
            totalPrice += priceRule.getPricePerUnit();
        }

        return PriceResultResponse.builder()
                .totalPrice(totalPrice)
                .priceResults(priceResults)
                .build();
    }

    private PriceRule getRuleForTimeInterval(
            List<PriceRule> applicableRules,
            LocalTime startTime, LocalTime endTime
    ) {
        PriceRule rule;

        // Rule for Time Interval in Special Date
        rule = applicableRules.stream()
                .filter(r -> r.getDate() != null && r.getStartTime() != null && r.getEndTime() != null)
                .findAny().orElse(null);

        if (rule != null && (
                rule.getStartTime().isBefore(endTime)
                        && rule.getEndTime().isAfter(startTime)
        )) {
            return rule;
        }

        // Rule for Special Date
        rule = applicableRules.stream()
                .filter(r -> r.getDate() != null)
                .findAny().orElse(null);

        if (rule != null) {
            return rule;
        }

        // Rule for Time Interval in Normal Date
        rule = applicableRules.stream()
                .filter(r -> r.getStartTime() != null && r.getEndTime() != null)
                .findAny().orElse(null);

        if (rule != null) {
            return rule;
        }

        // Rule for Normal Date
        return applicableRules.stream()
                .filter(r -> r.getDayFilter() != null && r.getDayFilter() != 0)
                .findAny().orElse(null);
    }

    private int calculateDateBit(LocalDate date) {
        // Monday: 1, Tuesday: 2
        int dayOfWeek = date.getDayOfWeek().getValue();
        int bitIndex = dayOfWeek - 1;

        // 2^n (2^bitIndex)
        return 1 << bitIndex;
    }

    private boolean isDayRuleApplicable(PriceRule rule, int dayBit) {
        if (rule.getDayFilter() == null || rule.getDayFilter() == 0) {
            return false;
        }

        return (rule.getDayFilter() & dayBit) > 0;
    }

    private PriceTableItemResponse toResponse(PriceTableItem item) {
        return PriceTableItemResponse.builder()
                .id(item.getId())
                .priceTableId(item.getPriceTable() != null ? item.getPriceTable().getId() : null)
                .studioTypeName(item.getStudioType() != null ? item.getStudioType().getName() : null)
                .defaultPrice(item.getDefaultPrice())
                .build();
    }
}
