package com.studio.booking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
    PENDING("Pending"),
    SUCCESS("Success"),
    FAILED("Failed");

    private final String code;
}
