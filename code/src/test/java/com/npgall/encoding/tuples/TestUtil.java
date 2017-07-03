package com.npgall.encoding.tuples;

/**
 * @author npgall
 */
public class TestUtil {

    public static String asBinaryString(byte[] bytes) {
        final int numBits = bytes.length * Byte.SIZE;
        StringBuilder sb = new StringBuilder(numBits);
        for( int i = 0; i < numBits; i++ ) {
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
            if (i % 8 == 7 && i < numBits - 1) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }
}
