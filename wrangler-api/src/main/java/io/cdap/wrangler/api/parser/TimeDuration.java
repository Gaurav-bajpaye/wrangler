package io.cdap.wrangler.api.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeDuration extends Token {
    private static final Pattern TIME_PATTERN = Pattern.compile("(?i)([0-9]*\\.?[0-9]+)\\s*(ms|s|min|ns)");
    private final long millis;

    public TimeDuration(String value) throws NumberFormatException {
        super(value);
        Matcher matcher = TIME_PATTERN.matcher(value.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid time duration format: " + value);
        }

        double number = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(2).toLowerCase();

        switch (unit) {
            case "ns":
                this.millis = (long) (number / 1_000_000);
                break;
            case "ms":
                this.millis = (long) number;
                break;
            case "s":
                this.millis = (long) (number * 1000);
                break;
            case "min":
                this.millis = (long) (number * 60 * 1000);
                break;
            default:
                throw new IllegalArgumentException("Unsupported time unit: " + unit);
        }
    }

    public long getMillis() {
        return millis;
    }

    @Override
    public String toString() {
        return value + " (" + millis + " ms)";
    }
}
