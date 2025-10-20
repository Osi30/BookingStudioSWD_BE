package com.studio.booking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StudioStatus {
    AVAILABLE,
    MAINTENANCE,
    DELETED,
    OCCUPIED
}
