package com.studio.booking.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdditionalTimePriceResponse {
    private double hourlyPrice;
    private long additionMinutes;
    private double extraFee;
    private String formula;
}
