package com.npgall.encoding.tuples;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

/**
 * @author npgall
 */
public class StringEncoderTest {

    private final StringEncoder stringEncoder = new StringEncoder();

    @Test
    public void testEncoding() {
        assertArrayEquals(asBytes(0x61, 0x0), encode("a")); // Latin small letter a
        assertArrayEquals(asBytes(0xC0, 0x80, 0x0), encode("\0")); // NUL character
        assertArrayEquals(asBytes(0xC3, 0xA0, 0x0), encode("\u00E0")); // Latin small letter a with grave
        assertArrayEquals(asBytes(0xED, 0xA0, 0xBD, 0xED, 0xB8, 0x82, 0x0), encode("\uD83D\uDE02")); // Face with tears of joy
    }

    @Test
    public void testRoundTripEncodingAndDecoding() {
        String input = "a\0\u00E0\uD83D\uDE02";
        byte[] encoded = encode(input);
        String decoded = decode(encoded);
        assertEquals(input, decoded);
    }


    // *********** Test utility methods.... ***********

    private byte[] asBytes(int... values) {
        byte[] bytes = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            int value = values[i];
            if (value > 255) {
                throw new IllegalArgumentException(String.valueOf(i));
            }
            bytes[i] = (byte)value;
        }
        return bytes;
    }

    private byte[] encode(String input) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        stringEncoder.encode(input, baos);
        return baos.toByteArray();
    }

    private String decode(byte[] bytes) {
        return stringEncoder.decode(new ByteArrayInputStream(bytes));
    }
}