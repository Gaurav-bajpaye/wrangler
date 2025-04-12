package io.cdap.wrangler.api.parser;

import org.junit.Test;
import static org.junit.Assert.*;

public class TimeDurationTest {

    @Test
    public void testValidTimeDurations() {
        assertEquals(150, new TimeDuration("150ms").getMillis());
        assertEquals(2000, new TimeDuration("2s").getMillis());
        assertEquals(60000, new TimeDuration("1min").getMillis());
        assertEquals(1, new TimeDuration("1000000ns").getMillis()); // 1ms = 1,000,000ns
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTimeFormat() {
        new TimeDuration("100xyz");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonNumericTime() {
        new TimeDuration("twoSec");
    }
}
