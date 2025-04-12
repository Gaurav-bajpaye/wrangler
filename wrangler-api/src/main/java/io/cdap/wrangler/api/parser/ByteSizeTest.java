package io.cdap.wrangler.api.parser;

import org.junit.Test;
import static org.junit.Assert.*;

public class ByteSizeTest {

    @Test
    public void testValidByteSizes() {
        assertEquals(10240, new ByteSize("10KB").getBytes());
        assertEquals(1048576, new ByteSize("1MB").getBytes());
        assertEquals(1073741824L, new ByteSize("1GB").getBytes());
        assertEquals(1099511627776L, new ByteSize("1TB").getBytes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidByteSizeFormat() {
        new ByteSize("10XY");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonNumericByteSize() {
        new ByteSize("tenMB");
    }
}
