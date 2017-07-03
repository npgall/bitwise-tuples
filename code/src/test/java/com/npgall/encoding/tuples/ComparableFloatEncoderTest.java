package com.npgall.encoding.tuples;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.npgall.encoding.tuples.TestUtil.asBinaryString;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link ComparableFloatEncoderTest}.
 *
 * @author npgall
 */
public class ComparableFloatEncoderTest {

    private final ComparableFloatEncoder encoder = new ComparableFloatEncoder();

    @Test
    public void testEncode() throws IOException {
        // NaN is a special case...
        assertEquals("11111111 11000000 00000000 00000000", encodeAsString(Float.NaN));
        // For the rest, positive and negative representations are bit-flipped mirror images of each other...
        assertEquals("11111111 10000000 00000000 00000000", encodeAsString(Float.POSITIVE_INFINITY));
        assertEquals("11111111 01111111 11111111 11111111", encodeAsString(Float.MAX_VALUE));
        assertEquals("11000011 10000000 00110011 00110011", encodeAsString(256.4F));
        assertEquals("11000011 10000000 00100110 01100110", encodeAsString(256.3F));
        assertEquals("11000011 00000001 00000000 00000000", encodeAsString(129));
        assertEquals("11000011 00000000 00000000 00000000", encodeAsString(128));
        assertEquals("11000010 11111110 00000000 00000000", encodeAsString(127));
        assertEquals("10111111 10000000 00000000 00000000", encodeAsString(1));
        assertEquals("10000000 00000000 00000000 00000000", encodeAsString(0.0F));
        assertEquals("01111111 11111111 11111111 11111111", encodeAsString(-0.0F));
        assertEquals("01000000 01111111 11111111 11111111", encodeAsString(-1));
        assertEquals("00111101 00000001 11111111 11111111", encodeAsString(-127));
        assertEquals("00111100 11111111 11111111 11111111", encodeAsString(-128));
        assertEquals("00111100 11111110 11111111 11111111", encodeAsString(-129));
        assertEquals("00111100 01111111 11011001 10011001", encodeAsString(-256.3F));
        assertEquals("00111100 01111111 11001100 11001100", encodeAsString(-256.4F));
        assertEquals("00000000 10000000 00000000 00000000", encodeAsString(-Float.MAX_VALUE));
        assertEquals("00000000 01111111 11111111 11111111", encodeAsString(Float.NEGATIVE_INFINITY));
    }

    @Test
    public void testRoundTrip() throws IOException {
        assertEquals(Float.NaN, encodeDecodeRoundTrip(Float.NaN), 0.001F);
        assertEquals(Float.POSITIVE_INFINITY, encodeDecodeRoundTrip(Float.POSITIVE_INFINITY), 0.001F);
        assertEquals(Float.MAX_VALUE, encodeDecodeRoundTrip(Float.MAX_VALUE), 0.001F);
        assertEquals(256, encodeDecodeRoundTrip(256), 0.001F);
        assertEquals(255, encodeDecodeRoundTrip(255), 0.001F);
        assertEquals(129, encodeDecodeRoundTrip(129), 0.001F);
        assertEquals(128, encodeDecodeRoundTrip(128), 0.001F);
        assertEquals(127, encodeDecodeRoundTrip(127), 0.001F);
        assertEquals(128, encodeDecodeRoundTrip(128), 0.001F);
        assertEquals(1, encodeDecodeRoundTrip(1), 0.001F);
        assertEquals(Float.floatToIntBits(0.0F), Float.floatToIntBits(encodeDecodeRoundTrip(0.0F))); // test for exact equivalence
        assertEquals(Float.floatToIntBits(-0.0F), Float.floatToIntBits(encodeDecodeRoundTrip(-0.0F))); // test for exact equivalence
        assertEquals(-1, encodeDecodeRoundTrip(-1), 0.001F);
        assertEquals(-127, encodeDecodeRoundTrip(-127), 0.001F);
        assertEquals(-128, encodeDecodeRoundTrip(-128), 0.001F);
        assertEquals(-129, encodeDecodeRoundTrip(-129), 0.001F);
        assertEquals(-255, encodeDecodeRoundTrip(-255), 0.001F);
        assertEquals(-256, encodeDecodeRoundTrip(-256), 0.001F);
        assertEquals(-Float.MAX_VALUE, encodeDecodeRoundTrip(-Float.MAX_VALUE), 0.001F);
        assertEquals(Float.NEGATIVE_INFINITY, encodeDecodeRoundTrip(Float.NEGATIVE_INFINITY), 0.001F);
    }


    // ===============================
    // === Test utility methods... ===
    // ===============================

    private byte[] encode(float value) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        encoder.encode(value, baos);
        return baos.toByteArray();
    }

    private String encodeAsString(float value) throws IOException {
        return asBinaryString(encode(value));
    }

    private float encodeDecodeRoundTrip(float value) throws IOException {
        return encoder.decode(new ByteArrayInputStream(encode(value)));
    }
}