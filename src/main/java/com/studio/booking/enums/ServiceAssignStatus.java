package com.studio.booking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceAssignStatus {
    ACTIVE,
    CANCELLED,
    AWAITING_REFUND,
}
