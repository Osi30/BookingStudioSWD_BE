package com.studio.booking.dtos.response;

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
public class PriceTableResponse {
    private String id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer priority;
    private PriceTableStatus status;
}
