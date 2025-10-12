package com.studio.booking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PriceTableStatus {
    COMING_SOON,
    IS_HAPPENING,
    ENDED,
    DELETED
}
