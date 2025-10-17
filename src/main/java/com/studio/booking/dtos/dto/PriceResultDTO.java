package com.studio.booking.dtos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PriceResultDTO {
    private LocalTime startTime;
    private LocalTime endTime;
    private Double price;
}
