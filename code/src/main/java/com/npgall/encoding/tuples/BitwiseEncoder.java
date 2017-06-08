package com.npgall.encoding.tuples;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for encoders which can encode and decode data types to/from binary.
 *
 * @author npgall
 */
public interface BitwiseEncoder<T> {

    void encode(T source, OutputStream sink);

    T decode(InputStream source);
}
