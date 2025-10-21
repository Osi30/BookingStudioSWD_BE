package com.studio.booking.dtos.request;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class AdditionalTimePriceRequest {
    private String studioTypeId;
    private LocalDateTime atTime;
    private long additionMinutes;
}
