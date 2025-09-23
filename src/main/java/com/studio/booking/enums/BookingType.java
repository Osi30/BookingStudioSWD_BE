package com.studio.booking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookingType {
    PAY_FULL("Customer pay full"),
    DEPOSIT("Customer deposit"),
    ;

    private final String description;
}
