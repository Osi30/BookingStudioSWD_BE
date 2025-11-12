package com.studio.booking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AssignStatus {
    COMING_SOON("Coming Soon"),
    IS_HAPPENING("Is Happening"),
    ENDED("Ended"),
    CANCELLED("Cancelled"),
    NO_SHOWING("No Showing")
    ;

    private final String description;
}
