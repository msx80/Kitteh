package com.github.msx80.kitteh.utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {
	
    public static void readFully(InputStream in, byte b[], int off, int len) throws IOException {
        if (len < 0)
            throw new IndexOutOfBoundsException();
        int n = 0;
        while (n < len) {
            int count = in.read(b, off + n, len - n);
            if (count < 0)
                throw new EOFException();
            n += count;
        }
    }
    
    public static void readFully(InputStream in, byte b[]) throws IOException {
        readFully(in, b, 0, b.length);
    }
}
