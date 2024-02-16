package de.hits.prison.server.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NumberUtil {

    public static String formatValue(int value) {
        return formatValue(new BigDecimal(value));
    }

    public static String formatValue(long value) {
        return formatValue(new BigDecimal(value));
    }

    public static String formatValue(double value) {
        return formatValue(new BigDecimal(value));
    }

    public static String formatValue(float value) {
        return formatValue(new BigDecimal(value));
    }

    public static String formatValue(BigInteger value) {
        return formatValue(new BigDecimal(value));
    }

    public static String formatValue(BigDecimal value) {
        if (value == null) {
            return "-";
        }

        if (value.compareTo(BigDecimal.valueOf(1_000)) < 0) {
            return value.toString();
        } else if (value.compareTo(BigDecimal.valueOf(1_000_000)) < 0) {
            return format(value, 3, "k");
        } else if (value.compareTo(BigDecimal.valueOf(1_000_000_000)) < 0) {
            return format(value, 6, "m");
        } else if (value.compareTo(BigDecimal.valueOf(1_000_000_000_000L)) < 0) {
            return format(value, 9, "b");
        } else {
            return format(value);
        }
    }

    private static String format(BigDecimal value, int exp, String suffix) {
        NumberFormat format = new DecimalFormat("#.##");
        format.setRoundingMode(RoundingMode.FLOOR);
        double scaledValue = value.doubleValue() / Math.pow(10, exp);
        return format.format(scaledValue) + suffix;
    }

    private static String format(BigDecimal value) {
        NumberFormat format = new DecimalFormat("#.####E0");
        format.setRoundingMode(RoundingMode.FLOOR);
        return format.format(value).replace("E", "x10^");
    }

}
