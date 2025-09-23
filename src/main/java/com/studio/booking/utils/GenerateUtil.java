package com.studio.booking.utils;

import org.apache.commons.text.RandomStringGenerator;

public class GenerateUtil {
    private GenerateUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String generateRandomWords(int length) {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('a', 'z')
                .get();

        return generator.generate(length).toUpperCase();
    }
}
