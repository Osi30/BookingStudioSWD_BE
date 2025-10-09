package com.studio.booking.dtos.request;

import com.studio.booking.enums.PriceUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPriceRuleRequest {
    private String priceTableItemId;
    private Integer dayFilter;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double pricePerUnit;
    private PriceUnit unit;
    private LocalDate date;
}
