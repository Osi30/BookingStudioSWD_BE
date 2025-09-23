package com.studio.booking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentType {
    FULL_PAYMENT("Full Payment"),
    FINAL_PAYMENT("Final Payment"),
    ADDITION_PAYMENT("Additional Payment"),
    DEPOSIT("Deposit Payment"),
    REFUND_PAYMENT("Refund Payment"),
    ;

    private final String code;
}
