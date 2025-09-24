package com.studio.booking.utils;

import java.util.Collection;

public class Validation {
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidCollection(Collection<?> collection) {
        if (collection == null) {
            return false;
        }
        return !collection.isEmpty();
    }

    public static boolean isValidNumber(Object value) {
        if (isNullOrEmpty(value.toString())) {
            return false;
        }
        try {
            Double.parseDouble(value.toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
