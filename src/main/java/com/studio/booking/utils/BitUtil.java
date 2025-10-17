package com.studio.booking.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class BitUtil {

    /// Return bit for specific date
    public static int calculateDateBit(LocalDate date) {
        return calculateDayBit(date.getDayOfWeek());
    }

    public static int calculateDayBit(DayOfWeek dayOfWeek) {
        // Monday: 1, Tuesday: 2
        int bitIndex = dayOfWeek.getValue() - 1;

        // 2^n (2^bitIndex)
        return 1 << bitIndex;
    }
}
