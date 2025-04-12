package io.cdap.wrangler.executor.directives.aggregate;

import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.Text;
import io.cdap.wrangler.api.executor.DirectiveContext;
import io.cdap.wrangler.api.executor.ExecutorContext;
import io.cdap.wrangler.api.row.Row;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class AggregateStatsTest {

    @Test
    public void testAggregateTotalMBSeconds() throws Exception {
        AggregateStats directive = new AggregateStats();

        directive.initialize(new DirectiveContext(), Arrays.asList(
                new ColumnName("size"), new ColumnName("time"),
                new Text("total_size_mb"), new Text("total_time_sec"),
                new Text("MB"), new Text("s"), new Text("total")
        ));

        List<Row> result = directive.execute(Arrays.asList(
                new Row("size", "1024KB").add("time", "1s"),
                new Row("size", "1MB").add("time", "500ms")
        ), new ExecutorContext());

        Row output = result.get(0);
        assertEquals(2.0, (double) output.getValue("total_size_mb"), 0.001);
        assertEquals(1.5, (double) output.getValue("total_time_sec"), 0.001);
    }

    @Test
    public void testAggregateAverageGBMinutes() throws Exception {
        AggregateStats directive = new AggregateStats();

        directive.initialize(new DirectiveContext(), Arrays.asList(
                new ColumnName("size"), new ColumnName("time"),
                new Text("avg_size_gb"), new Text("avg_time_min"),
                new Text("GB"), new Text("min"), new Text("average")
        ));

        List<Row> result = directive.execute(Arrays.asList(
                new Row("size", "2GB").add("time", "2min"),
                new Row("size", "1GB").add("time", "1min")
        ), new ExecutorContext());

        Row output = result.get(0);
        assertEquals(1.5, (double) output.getValue("avg_size_gb"), 0.001);
        assertEquals(1.5, (double) output.getValue("avg_time_min"), 0.001);
    }
}
