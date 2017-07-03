package com.npgall.encoding.tuples;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author npgall
 */
public class ComparableFloatEncoder implements BitwiseEncoder<Float> {

    private static final int SIGN_MASK = 0x80000000;
    private static final int NUM_BITS = Integer.SIZE;
    private static final int ONE_BYTE = 8;
    private static final int INITIAL_BITS_TO_SHIFT = NUM_BITS - ONE_BYTE;
    private static final int ALL_BITS_MASK = Integer.MAX_VALUE;

    @Override
    public void encode(Float value, OutputStream output) throws IOException {
        encodeExcessK(value, output);
    }

    @Override
    public Float decode(InputStream input) throws IOException {
        return decodeExcessK(input);
    }

    public static void encodeExcessK(float value, OutputStream output) throws IOException {
        int intValue = Float.floatToIntBits(value);

        // If intValue is positive, do nothing.
        // If intValue is negative, flip all the bits except for the sign bit.
        intValue ^= (intValue >> 31) & ALL_BITS_MASK;

        intValue ^= SIGN_MASK; // flip MSB (the sign bit)

        for (int bitsToShift = INITIAL_BITS_TO_SHIFT; bitsToShift >= 0; bitsToShift -= ONE_BYTE) {
            output.write((byte)(intValue >>> bitsToShift));
        }
    }

    public static float decodeExcessK(InputStream input) throws IOException {
        int intValue = 0;
        for (int bitsToShift = INITIAL_BITS_TO_SHIFT; bitsToShift >= 0; bitsToShift -= ONE_BYTE) {
            int ch = input.read();
            if (ch < 0) {
                throw new EOFException();
            }
            intValue += ch << bitsToShift;
        }
        intValue ^= SIGN_MASK; // flip MSB (the sign bit)

        // Undo the bit-flipping for negative numbers...
        intValue ^= (intValue >> 31) & ALL_BITS_MASK;

        return Float.intBitsToFloat(intValue);
    }
}
