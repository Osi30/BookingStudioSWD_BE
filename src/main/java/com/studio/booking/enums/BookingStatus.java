package com.studio.booking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookingStatus {
    IN_PROGRESS("Booking in progress"),
    COMPLETED("Booking completed"),
    CANCELLED("Booking cancelled"),
    AWAITING_REFUND("Awaiting cancelled refund"),
    AWAITING_PAYMENT("Awaiting payment"),
    CONFIRMED("Booking confirmed"),
    ;

    private final String description;
}
