package com.civicportal.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtil {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ISO_LOCAL_DATE;

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private DateTimeUtil() {
    }

    public static LocalDate today() {
        return LocalDate.now();
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static String formatDate(LocalDate date) {
        return date == null ? null : date.format(DATE_FORMAT);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DATE_TIME_FORMAT);
    }
}