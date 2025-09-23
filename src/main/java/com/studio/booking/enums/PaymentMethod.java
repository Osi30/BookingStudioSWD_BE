package com.studio.booking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    VNPAY("vnpay"),
    MOMO("momo"),
    CASH("cash");

    private final String code;
}
