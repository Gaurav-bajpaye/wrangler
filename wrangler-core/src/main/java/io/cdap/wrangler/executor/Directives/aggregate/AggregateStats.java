package io.cdap.wrangler.executor.directives.aggregate;

import io.cdap.wrangler.api.*;
import io.cdap.wrangler.api.parser.*;
import io.cdap.wrangler.api.row.Row;
import io.cdap.wrangler.api.executor.Directive;
import io.cdap.wrangler.api.executor.DirectiveContext;

import java.util.*;

/**
 * Directive to aggregate byte sizes and time durations.
 * Usage: aggregate-stats :size_col :time_col total_size_mb total_time_sec [MB|GB] [s|min] [total|average]
 */
public class AggregateStats implements Directive {
    private String sizeInputCol;
    private String timeInputCol;
    private String sizeOutputCol;
    private String timeOutputCol;
    private String sizeUnit = "MB"; // default
    private String timeUnit = "s";  // default
    private String aggregationType = "total"; // default

    private long totalBytes = 0;
    private long totalMillis = 0;
    private int count = 0;

    @Override
    public UsageDefinition define() {
        return UsageDefinition.builder("aggregate-stats")
            .addRequiredArg("sourceByteSizeColumn", ColumnName.class)
            .addRequiredArg("sourceTimeDurationColumn", ColumnName.class)
            .addRequiredArg("outputByteSizeColumn", Text.class)
            .addRequiredArg("outputTimeDurationColumn", Text.class)
            .addOptionalArg("byteUnit", Text.class)
            .addOptionalArg("timeUnit", Text.class)
            .addOptionalArg("aggregationType", Text.class)
            .build();
    }

    @Override
    public void initialize(DirectiveContext context, List<Token> args) throws DirectiveParseException {
        this.sizeInputCol = ((ColumnName) args.get(0)).value();
        this.timeInputCol = ((ColumnName) args.get(1)).value();
        this.sizeOutputCol = ((Text) args.get(2)).value();
        this.timeOutputCol = ((Text) args.get(3)).value();

        if (args.size() > 4) {
            this.sizeUnit = ((Text) args.get(4)).value().toUpperCase();
        }

        if (args.size() > 5) {
            this.timeUnit = ((Text) args.get(5)).value().toLowerCase();
        }

        if (args.size() > 6) {
            this.aggregationType = ((Text) args.get(6)).value().toLowerCase();
        }
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
        for (Row row : rows) {
            Object sizeVal = row.getValue(sizeInputCol);
            Object timeVal = row.getValue(timeInputCol);

            try {
                long bytes = parseByteSize(sizeVal.toString());
                long millis = parseTimeDuration(timeVal.toString());
                totalBytes += bytes;
                totalMillis += millis;
                count++;
            } catch (Exception e) {
                throw new DirectiveExecutionException("Failed to parse size/time: " + e.getMessage());
            }
        }

        double resultBytes = aggregationType.equals("average") ? (double) totalBytes / count : totalBytes;
        double resultMillis = aggregationType.equals("average") ? (double) totalMillis / count : totalMillis;

        // Convert units
        resultBytes = convertBytes(resultBytes, sizeUnit);
        resultMillis = convertMillis(resultMillis, timeUnit);

        Row result = new Row();
        result.add(sizeOutputCol, resultBytes);
        result.add(timeOutputCol, resultMillis);

        return Collections.singletonList(result);
    }

    private long parseByteSize(String val) {
        return new ByteSize(val).getBytes();
    }

    private long parseTimeDuration(String val) {
        return new TimeDuration(val).getMillis();
    }

    private double convertBytes(double bytes, String unit) {
        switch (unit) {
            case "KB":
                return bytes / 1024;
            case "MB":
                return bytes / (1024 * 1024);
            case "GB":
                return bytes / (1024.0 * 1024 * 1024);
            default:
                return bytes;
        }
    }

    private double convertMillis(double millis, String unit) {
        switch (unit) {
            case "s":
                return millis / 1000.0;
            case "min":
                return millis / (1000.0 * 60);
            default:
                return millis;
        }
    }
}
