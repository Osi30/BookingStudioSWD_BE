package com.studio.booking.dtos.request;

import com.studio.booking.enums.PriceUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceRuleRequest {
    private String priceTableItemId;
    private List<DayOfWeek> daysOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double pricePerUnit;
    private PriceUnit unit;
    private LocalDate date;
}
