package com.civicportal.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public final class ComplaintNumberGenerator {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.BASIC_ISO_DATE;

    private ComplaintNumberGenerator() {
    }

    public static String generate() {
        String date = LocalDate.now().format(DATE_FORMAT);
        String suffix = UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase(Locale.ROOT);

        return "CP-" + date + "-" + suffix;
    }
}
