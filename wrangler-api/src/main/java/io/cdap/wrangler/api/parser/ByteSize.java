package io.cdap.wrangler.api.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ByteSize extends Token {
    private static final Pattern BYTE_PATTERN = Pattern.compile("(?i)([0-9]*\\.?[0-9]+)\\s*([kKmMgGtT][bB])");
    private final long bytes;

    public ByteSize(String value) throws NumberFormatException {
        super(value);
        Matcher matcher = BYTE_PATTERN.matcher(value.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid byte size format: " + value);
        }

        double number = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(2).toUpperCase();

        switch (unit) {
            case "KB":
                this.bytes = (long) (number * 1024);
                break;
            case "MB":
                this.bytes = (long) (number * 1024 * 1024);
                break;
            case "GB":
                this.bytes = (long) (number * 1024 * 1024 * 1024);
                break;
            case "TB":
                this.bytes = (long) (number * 1024L * 1024L * 1024L * 1024L);
                break;
            default:
                throw new IllegalArgumentException("Unsupported byte unit: " + unit);
        }
    }

    public long getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return value + " (" + bytes + " bytes)";
    }
}
