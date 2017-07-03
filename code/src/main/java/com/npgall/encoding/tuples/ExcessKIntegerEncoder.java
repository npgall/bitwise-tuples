package com.npgall.encoding.tuples;

import java.io.*;

/**
 * @author npgall
 */
public class ExcessKIntegerEncoder implements BitwiseEncoder<Integer> {

    private static final int SIGN_MASK = 0x80000000;
    private static final int NUM_BITS = Integer.SIZE;
    private static final int ONE_BYTE = 8;
    private static final int INITIAL_BITS_TO_SHIFT = NUM_BITS - ONE_BYTE;

    @Override
    public void encode(Integer value, OutputStream output) throws IOException {
        encodeExcessK(value, output);
    }

    @Override
    public Integer decode(InputStream input) throws IOException {
        return decodeExcessK(input);
    }

    public static void encodeExcessK(int value, OutputStream output) throws IOException {
        value ^= SIGN_MASK; // flip MSB (the sign bit)
        for (int bitsToShift = INITIAL_BITS_TO_SHIFT; bitsToShift >= 0; bitsToShift -= ONE_BYTE) {
            output.write((byte)(value >>> bitsToShift));
        }
    }

    public static int decodeExcessK(InputStream input) throws IOException {
        int value = 0;
        for (int bitsToShift = INITIAL_BITS_TO_SHIFT; bitsToShift >= 0; bitsToShift -= ONE_BYTE) {
            int ch = input.read();
            if (ch < 0) {
                throw new EOFException();
            }
            value += ch << bitsToShift;
        }
        value ^= SIGN_MASK; // flip MSB (the sign bit)
        return value;
    }
}
