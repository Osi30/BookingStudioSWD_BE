package com.studio.booking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountIdentifier {
    EMAIL("email"),
    USERNAME("username"),
    ALL("all"),
    ;

    private final String value;
}
