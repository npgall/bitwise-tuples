package com.npgall.encoding.tuples;

import java.io.*;

/**
 * Encodes and decodes Strings and {@link CharSequence}s to/from
 * <a href="https://en.wikipedia.org/wiki/UTF-8#Modified_UTF-8">Modified-UTF8</a>-encoded binary.
 * The binary encoding is terminated with a null byte.
 *
 * @author npgall
 */
public class ModifiedUtf8Encoder implements BitwiseEncoder<CharSequence> {

    @Override
    public void encode(CharSequence value, OutputStream output) {
        try {
            encodeModifiedUtf8(value, output);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encode character sequence to Modified-UTF8 output stream", e);
        }
    }

    @Override
    public CharSequence decode(InputStream input) {
        try {
            StringBuilder sink = new StringBuilder();
            decodeModifiedUtf8(input, sink);
            return sink;
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to decode Modified-UTF8 bytes from input stream", e);
        }
    }

    public static void encodeModifiedUtf8(CharSequence value, OutputStream output) throws IOException {
        final int numChars = value.length();
        char c; ;
        for (int i = 0; i < numChars; i++){
            c = value.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                output.write((byte) c);

            } else if (c > 0x07FF) {
                output.write((byte) (0xE0 | ((c >> 12) & 0x0F)));
                output.write((byte) (0x80 | ((c >>  6) & 0x3F)));
                output.write((byte) (0x80 | (c & 0x3F)));
            } else {
                output.write((byte) (0xC0 | ((c >>  6) & 0x1F)));
                output.write((byte) (0x80 | (c & 0x3F)));
            }
        }
        output.write(0); // null terminator byte
    }

    public static void decodeModifiedUtf8(InputStream value, Appendable output) throws IOException {
        int char1, char2, char3;

        while (true) {
            char1 = (int) readByte(value) & 0xff;
            if (char1 == 0) { // null terminator byte
                break;
            }
            switch (char1 >> 4) {
                case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
                    /* 0xxxxxxx*/
                    output.append((char) char1);
                    break;
                case 12: case 13:
                    /* 110x xxxx   10xx xxxx*/
                    char2 = (int) readByte(value);
                    if ((char2 & 0xC0) != 0x80) {
                        throw new UTFDataFormatException("Malformed Modified-UTF8 input");
                    }
                    output.append((char) (((char1 & 0x1F) << 6) | (char2 & 0x3F)));
                    break;
                case 14:
                    /* 1110 xxxx  10xx xxxx  10xx xxxx */
                    char2 = (int) readByte(value);
                    char3 = (int) readByte(value);
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80)) {
                        throw new UTFDataFormatException("Malformed Modified-UTF8 input");
                    }
                    output.append((char) (((char1 & 0x0F) << 12) | ((char2 & 0x3F) << 6) | ((char3 & 0x3F))));
                    break;
                default:
                    /* 10xx xxxx,  1111 xxxx */
                    throw new UTFDataFormatException("Malformed Modified-UTF8 input");
            }
        }
    }

    private static byte readByte(InputStream inputStream) throws IOException {
        int candidate = inputStream.read();
        if (candidate < 0) {
            throw new EOFException("Modified-UTF8 input was truncated");
        }
        return (byte) candidate;
    }
}
