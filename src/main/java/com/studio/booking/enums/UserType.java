package com.studio.booking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserType {
    PERSONAL("Customer is a personal user"),
    ORGANIZATION("Customer is an organization user"),;

    private final String description;
}
