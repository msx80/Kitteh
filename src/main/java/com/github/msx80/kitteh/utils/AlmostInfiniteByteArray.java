package com.github.msx80.kitteh.utils;

import java.nio.charset.Charset;

public class AlmostInfiniteByteArray {

    private byte[] array;
    private int size;

    public AlmostInfiniteByteArray(int cap) {
        array = new byte[cap];
            size = 0;
    }

    public int get(int pos) {
        if (pos>=size) throw new ArrayIndexOutOfBoundsException();
        return array[pos];
    }

    public void set(int pos, byte val) {
        if (pos>=size) {
            if (pos>=array.length) {
                byte[] newarray = new byte[(pos+1)*5/4];
                System.arraycopy(array, 0, newarray, 0, size);
                array = newarray;
            }
            size = pos+1;
        }
        array[pos] = val;
    }
    
    public int size()
    {
    	return size;
    }
    
    public void append(byte val)
    {
    	set(size, val);
    }

	public String encodeAsString(Charset cs) {
		
		return new String(array, 0, size, cs);
	}
}