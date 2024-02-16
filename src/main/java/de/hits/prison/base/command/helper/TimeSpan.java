package de.hits.prison.base.command.helper;

import java.util.concurrent.TimeUnit;

public class TimeSpan {
    private final long value;
    private final TimeUnit unit;

    public TimeSpan(long value, TimeUnit unit) {
        this.value = value;
        this.unit = unit;
    }

    public long toLong() {
        return unit.toMillis(value);
    }

    public static TimeSpan fromString(String value) {
        char lastChar = value.charAt(value.length() - 1);
        long numericValue = Long.parseLong(value.substring(0, value.length() - 1));
        return switch (lastChar) {
            case 'd' -> new TimeSpan(numericValue, TimeUnit.DAYS);
            case 'h' -> new TimeSpan(numericValue, TimeUnit.HOURS);
            case 'm' -> new TimeSpan(numericValue, TimeUnit.MINUTES);
            case 's' -> new TimeSpan(numericValue, TimeUnit.SECONDS);
            default -> throw new IllegalArgumentException("Invalid time unit: " + lastChar);
        };
    }
}