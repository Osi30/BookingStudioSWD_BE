package com.studio.booking.dtos.request;

import com.studio.booking.enums.PriceTableStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceTableRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer priority;
    private PriceTableStatus status;
}
