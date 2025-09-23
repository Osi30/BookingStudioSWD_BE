package com.studio.booking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountRole {
    CUSTOMER("ROLE_CUSTOMER"),
    STAFF("ROLE_STAFF"),
    ADMIN("ROLE_ADMIN");

    private final String role;
}
