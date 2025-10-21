package com.studio.booking.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StudioAssignAdditionTimeResponse {
    private String assignId;

    private LocalDateTime oldEndTime;
    private LocalDateTime newEndTime;

    private long addedMinutes;
    private double addedFee;
    private double newStudioAmount;

    private String bookingId;
    private Double newBookingTotal;
}
