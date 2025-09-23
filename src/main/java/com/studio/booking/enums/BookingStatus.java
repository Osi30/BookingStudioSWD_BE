package com.studio.booking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookingStatus {
    IN_PROGRESS("Booking in progress"),
    COMPLETED("Booking completed"),
    CANCELLED("Booking cancelled");

    private final String description;
}
