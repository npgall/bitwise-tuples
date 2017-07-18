package com.npgall.encoding.tuples;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.npgall.encoding.tuples.TestUtil.asBinaryString;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link ComparableDoubleEncoder}.
 *
 * @author npgall
 */
public class ComparableDoubleEncoderTest {

    private final ComparableDoubleEncoder encoder = new ComparableDoubleEncoder();

    @Test
    public void testEncode() throws IOException {
        // NaN is a special case...
        assertEquals("11111111 11111000 00000000 00000000 00000000 00000000 00000000 00000000", encodeAsString(Double.NaN));
        // For the rest, positive and negative representations are bit-flipped mirror images of each other...
        assertEquals("11111111 11110000 00000000 00000000 00000000 00000000 00000000 00000000", encodeAsString(Double.POSITIVE_INFINITY));
        assertEquals("11111111 11101111 11111111 11111111 11111111 11111111 11111111 11111111", encodeAsString(Double.MAX_VALUE));
        assertEquals("11000000 01110000 00000110 01100110 01100110 01100110 01100110 01100110", encodeAsString(256.4));
        assertEquals("11000000 01110000 00000100 11001100 11001100 11001100 11001100 11001101", encodeAsString(256.3));
        assertEquals("11000000 01100000 00100000 00000000 00000000 00000000 00000000 00000000", encodeAsString(129));
        assertEquals("11000000 01100000 00000000 00000000 00000000 00000000 00000000 00000000", encodeAsString(128));
        assertEquals("11000000 01011111 11000000 00000000 00000000 00000000 00000000 00000000", encodeAsString(127));
        assertEquals("10111111 11110000 00000000 00000000 00000000 00000000 00000000 00000000", encodeAsString(1));
        assertEquals("10000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000", encodeAsString(0.0));
        assertEquals("01111111 11111111 11111111 11111111 11111111 11111111 11111111 11111111", encodeAsString(-0.0));
        assertEquals("01000000 00001111 11111111 11111111 11111111 11111111 11111111 11111111", encodeAsString(-1));
        assertEquals("00111111 10100000 00111111 11111111 11111111 11111111 11111111 11111111", encodeAsString(-127));
        assertEquals("00111111 10011111 11111111 11111111 11111111 11111111 11111111 11111111", encodeAsString(-128));
        assertEquals("00111111 10011111 11011111 11111111 11111111 11111111 11111111 11111111", encodeAsString(-129));
        assertEquals("00111111 10001111 11111011 00110011 00110011 00110011 00110011 00110010", encodeAsString(-256.3));
        assertEquals("00111111 10001111 11111001 10011001 10011001 10011001 10011001 10011001", encodeAsString(-256.4));
        assertEquals("00000000 00010000 00000000 00000000 00000000 00000000 00000000 00000000", encodeAsString(-Double.MAX_VALUE));
        assertEquals("00000000 00001111 11111111 11111111 11111111 11111111 11111111 11111111", encodeAsString(Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testRoundTrip() throws IOException {
        assertEquals(Double.NaN, encodeDecodeRoundTrip(Double.NaN), 0.001);
        assertEquals(Double.POSITIVE_INFINITY, encodeDecodeRoundTrip(Double.POSITIVE_INFINITY), 0.001);
        assertEquals(Double.MAX_VALUE, encodeDecodeRoundTrip(Double.MAX_VALUE), 0.001);
        assertEquals(256, encodeDecodeRoundTrip(256), 0.001);
        assertEquals(255, encodeDecodeRoundTrip(255), 0.001);
        assertEquals(129, encodeDecodeRoundTrip(129), 0.001);
        assertEquals(128, encodeDecodeRoundTrip(128), 0.001);
        assertEquals(127, encodeDecodeRoundTrip(127), 0.001);
        assertEquals(128, encodeDecodeRoundTrip(128), 0.001);
        assertEquals(1, encodeDecodeRoundTrip(1), 0.001);
        assertEquals(Double.doubleToLongBits(0.0), Double.doubleToLongBits(encodeDecodeRoundTrip(0.0))); // test for exact equivalence
        assertEquals(Double.doubleToLongBits(-0.0), Double.doubleToLongBits(encodeDecodeRoundTrip(-0.0))); // test for exact equivalence
        assertEquals(-1, encodeDecodeRoundTrip(-1), 0.001);
        assertEquals(-127, encodeDecodeRoundTrip(-127), 0.001);
        assertEquals(-128, encodeDecodeRoundTrip(-128), 0.001);
        assertEquals(-129, encodeDecodeRoundTrip(-129), 0.001);
        assertEquals(-255, encodeDecodeRoundTrip(-255), 0.001);
        assertEquals(-256, encodeDecodeRoundTrip(-256), 0.001);
        assertEquals(-Double.MAX_VALUE, encodeDecodeRoundTrip(-Double.MAX_VALUE), 0.001);
        assertEquals(Double.NEGATIVE_INFINITY, encodeDecodeRoundTrip(Double.NEGATIVE_INFINITY), 0.001);
    }


    // ===============================
    // === Test utility methods... ===
    // ===============================

    private byte[] encode(double value) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        encoder.encode(value, baos);
        return baos.toByteArray();
    }

    private String encodeAsString(double value) throws IOException {
        return asBinaryString(encode(value));
    }

    private double encodeDecodeRoundTrip(double value) throws IOException {
        return encoder.decode(new ByteArrayInputStream(encode(value)));
    }
}