package com.npgall.encoding.tuples;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author npgall
 */
public class ExcessKLongEncoder implements BitwiseEncoder<Long> {

    private static final long SIGN_MASK = 0x8000000000000000L;
    private static final int NUM_BITS = Long.SIZE;
    private static final int ONE_BYTE = 8;
    private static final int INITIAL_BITS_TO_SHIFT = NUM_BITS - ONE_BYTE;

    @Override
    public void encode(Long value, OutputStream output) throws IOException {
        encodeExcessK(value, output);
    }

    @Override
    public Long decode(InputStream input) throws IOException {
        return decodeExcessK(input);
    }

    public static void encodeExcessK(long value, OutputStream output) throws IOException {
        value ^= SIGN_MASK; // flip MSB (the sign bit)
        for (int bitsToShift = INITIAL_BITS_TO_SHIFT; bitsToShift >= 0; bitsToShift -= ONE_BYTE) {
            output.write((byte)(value >>> bitsToShift));
        }
    }

    public static long decodeExcessK(InputStream input) throws IOException {
        long value = 0;
        for (int bitsToShift = INITIAL_BITS_TO_SHIFT; bitsToShift >= 0; bitsToShift -= ONE_BYTE) {
            long ch = input.read();
            if (ch < 0) {
                throw new EOFException();
            }
            value += ch << bitsToShift;
        }
        value ^= SIGN_MASK; // flip MSB (the sign bit)
        return value;
    }
}
