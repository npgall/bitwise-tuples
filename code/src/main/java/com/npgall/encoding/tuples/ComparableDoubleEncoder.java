package com.npgall.encoding.tuples;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author npgall
 */
public class ComparableDoubleEncoder implements BitwiseEncoder<Double> {

    private static final long SIGN_MASK = 0x8000000000000000L;
    private static final int NUM_BITS = Long.SIZE;
    private static final int NUM_BITS_WITHOUT_SIGN = NUM_BITS - 1;
    private static final int ONE_BYTE = 8;
    private static final int INITIAL_BITS_TO_SHIFT = NUM_BITS - ONE_BYTE;
    private static final long ALL_BITS_EXCEPT_SIGN_MASK = Long.MAX_VALUE;

    @Override
    public void encode(Double value, OutputStream output) throws IOException {
        encodeExcessK(value, output);
    }

    @Override
    public Double decode(InputStream input) throws IOException {
        return decodeExcessK(input);
    }

    public static void encodeExcessK(double value, OutputStream output) throws IOException {
        long longValue = Double.doubleToLongBits(value);

        // If longValue is positive, do nothing.
        // If longValue is negative, flip all the bits except for the sign bit.
        longValue ^= (longValue >> NUM_BITS_WITHOUT_SIGN) & ALL_BITS_EXCEPT_SIGN_MASK;

        longValue ^= SIGN_MASK; // flip MSB (the sign bit)

        for (int bitsToShift = INITIAL_BITS_TO_SHIFT; bitsToShift >= 0; bitsToShift -= ONE_BYTE) {
            output.write((byte)(longValue >>> bitsToShift));
        }
    }

    public static double decodeExcessK(InputStream input) throws IOException {
        long longValue = 0;
        for (int bitsToShift = INITIAL_BITS_TO_SHIFT; bitsToShift >= 0; bitsToShift -= ONE_BYTE) {
            long ch = input.read();
            if (ch < 0) {
                throw new EOFException();
            }
            longValue += ch << bitsToShift;
        }
        longValue ^= SIGN_MASK; // flip MSB (the sign bit)

        // Undo the bit-flipping for negative numbers...
        longValue ^= (longValue >> NUM_BITS_WITHOUT_SIGN) & ALL_BITS_EXCEPT_SIGN_MASK;

        return Double.longBitsToDouble(longValue);
    }
}
