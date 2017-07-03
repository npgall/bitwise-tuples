package com.npgall.encoding.tuples;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link ModifiedUtf8Encoder}
 * @author npgall
 */
public class ModifiedUtf8EncoderTest {

    private final BitwiseEncoder<CharSequence> encoder = new ModifiedUtf8Encoder();

    @Test
    public void testEncode() throws IOException {
        assertArrayEquals(asBytes(0x61, 0x0), encode("a")); // Latin small letter a
        assertArrayEquals(asBytes(0xC0, 0x80, 0x0), encode("\0")); // NUL character
        assertArrayEquals(asBytes(0xC3, 0xA0, 0x0), encode("\u00E0")); // Latin small letter a with grave
        assertArrayEquals(asBytes(0xED, 0xA0, 0xBD, 0xED, 0xB8, 0x82, 0x0), encode("\uD83D\uDE02")); // Face with tears of joy
    }

    @Test
    public void testRoundTrip() throws IOException {
        String input = "a\0\u00E0\uD83D\uDE02";
        byte[] encoded = encode(input);
        CharSequence decoded = decode(encoded);
        assertEquals(input, decoded.toString());
    }


    // ===============================
    // === Test utility methods... ===
    // ===============================

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

    private byte[] encode(String input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        encoder.encode(input, baos);
        return baos.toByteArray();
    }

    private CharSequence decode(byte[] bytes) throws IOException {
        return encoder.decode(new ByteArrayInputStream(bytes));
    }
}