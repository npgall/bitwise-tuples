package com.npgall.encoding.tuples;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.npgall.encoding.tuples.TestUtil.asBinaryString;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link ExcessKLongEncoder}.
 *
 * @author npgall
 */
public class ExcessKLongEncoderTest {

    private final ExcessKLongEncoder encoder = new ExcessKLongEncoder();

    @Test
    public void testEncode() throws IOException {
        assertEquals("11111111 11111111 11111111 11111111 11111111 11111111 11111111 11111111", encodeAsString(Long.MAX_VALUE));
        assertEquals("10000000 00000000 00000000 00000000 00000000 00000000 00000001 00000000", encodeAsString(256));
        assertEquals("10000000 00000000 00000000 00000000 00000000 00000000 00000000 11111111", encodeAsString(255));
        assertEquals("10000000 00000000 00000000 00000000 00000000 00000000 00000000 10000001", encodeAsString(129));
        assertEquals("10000000 00000000 00000000 00000000 00000000 00000000 00000000 10000000", encodeAsString(128));
        assertEquals("10000000 00000000 00000000 00000000 00000000 00000000 00000000 01111111", encodeAsString(127));
        assertEquals("10000000 00000000 00000000 00000000 00000000 00000000 00000000 00000001", encodeAsString(1));
        assertEquals("10000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000", encodeAsString(0));
        assertEquals("01111111 11111111 11111111 11111111 11111111 11111111 11111111 11111111", encodeAsString(-1));
        assertEquals("01111111 11111111 11111111 11111111 11111111 11111111 11111111 10000001", encodeAsString(-127));
        assertEquals("01111111 11111111 11111111 11111111 11111111 11111111 11111111 10000000", encodeAsString(-128));
        assertEquals("01111111 11111111 11111111 11111111 11111111 11111111 11111111 01111111", encodeAsString(-129));
        assertEquals("01111111 11111111 11111111 11111111 11111111 11111111 11111111 00000001", encodeAsString(-255));
        assertEquals("01111111 11111111 11111111 11111111 11111111 11111111 11111111 00000000", encodeAsString(-256));
        assertEquals("00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000", encodeAsString(Long.MIN_VALUE));
    }

    @Test
    public void testRoundTrip() throws IOException {
        assertEquals(Long.MAX_VALUE, encodeDecodeRoundTrip(Long.MAX_VALUE));
        assertEquals(256, encodeDecodeRoundTrip(256));
        assertEquals(255, encodeDecodeRoundTrip(255));
        assertEquals(129, encodeDecodeRoundTrip(129));
        assertEquals(128, encodeDecodeRoundTrip(128));
        assertEquals(127, encodeDecodeRoundTrip(127));
        assertEquals(128, encodeDecodeRoundTrip(128));
        assertEquals(1, encodeDecodeRoundTrip(1));
        assertEquals(0, encodeDecodeRoundTrip(0));
        assertEquals(-1, encodeDecodeRoundTrip(-1));
        assertEquals(-127, encodeDecodeRoundTrip(-127));
        assertEquals(-128, encodeDecodeRoundTrip(-128));
        assertEquals(-129, encodeDecodeRoundTrip(-129));
        assertEquals(-255, encodeDecodeRoundTrip(-255));
        assertEquals(-256, encodeDecodeRoundTrip(-256));
        assertEquals(Long.MIN_VALUE, encodeDecodeRoundTrip(Long.MIN_VALUE));
    }


    // ===============================
    // === Test utility methods... ===
    // ===============================

    private byte[] encode(long value) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        encoder.encode(value, baos);
        return baos.toByteArray();
    }

    private String encodeAsString(long value) throws IOException {
        return asBinaryString(encode(value));
    }

    private long encodeDecodeRoundTrip(long value) throws IOException {
        return encoder.decode(new ByteArrayInputStream(encode(value)));
    }
}