package com.studio.booking.dtos.response;

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
public class PriceRuleResponse {
    private String id;
    private String priceTableItemId;
    private List<DayOfWeek> dayFilter;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double pricePerUnit;
    private PriceUnit unit;
    private LocalDate date;
    private Boolean isDeleted;
}
