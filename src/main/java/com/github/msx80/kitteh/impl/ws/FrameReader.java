package com.github.msx80.kitteh.impl.ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class FrameReader {

	public static final Charset UTF8 = Charset.forName("UTF-8");
	
	public static void sendText(OutputStream os, String text) throws IOException
	{
		System.out.println("Sending string: "+text);
		int head = 0b10000001;
		os.write(head);
		
		byte[] data = text.getBytes(UTF8);
		
		int len = data.length;
		if (len < 126) {
			os.write(len);
		}
		else if(len < 65536)
		{
			os.write(126);
			int h = (len & 0xFF00) >> 8;
			int l = (len & 0xFF);
			os.write(h);
			os.write(l);
		}
		else
		{
			throw new UnsupportedOperationException("message bigger than 65535 not implemented yet");
		}
		
		String k = new String(data, UTF8);
		System.out.println(k);
		System.out.println(k.equals(text));
		os.write(data);
		os.flush();
	}
	
	public static Object readFrame(InputStream in) throws IOException {
		
		try {
			int head = readByte(in);
			
			boolean fin = (head & 0b10000000) != 0;
			int opcode = (head & 0b1111);
			
			int second = readByte(in);
			boolean masked = (second & 0b10000000) != 0;
			int lengthCode = second & 0b01111111;
			long length = readLength(lengthCode, in);

			byte[] mask = null;
			if (masked)
			{
				mask = new byte[4];
				int num = in.read(mask);
				assert num == 4;
			}
			
			byte[] data = new byte[(int) length];
			int num = in.read(data);
			assert num == length;
			
			if (masked) {
				for (int i = 0; i < data.length; i++) {
					data[i] = (byte) (data[i] ^ mask[i % 4]);
				}
			}
			if(opcode == 1)
			{
				return new String(data, UTF8);
			}
			else if(opcode == 2)
			{
				return data;
			}
			else if(opcode == 8)
			{
				return null; // close
			}
			else if(opcode == 0)
			{
				throw new IOException("Continuation frames not supported");
			}
			else if((opcode == 9) || (opcode == 10))
			{
				// ping or pong. handle it and ask a new frame
				return readFrame(in);
			}
			else
			{
				throw new IOException("Unknown frames opcode "+opcode);
			}
				
		} 
		catch (ConnectionClosedException e) 
		{
			
			return null;
		}

	}

	private static int readByte(InputStream in) throws IOException, ConnectionClosedException {
		int r = in.read();
		if(r==-1) throw new ConnectionClosedException();
		return r;
	}

	private static long readLength(int lengthCode, InputStream in) throws IOException, ConnectionClosedException 
	{
		if(lengthCode == 126)
		{
			long size = 0;
			for (int i = 0; i < 2 ; i++) 
			{	
				size = size << 8;
				int x = readByte(in);
				size += x;
			}
			return size;
			
		}
		else if (lengthCode == 127)
		{
			long size = 0;
			for (int i = 0; i < 8 ; i++) 
			{	
				size = size << 8;
				int x = readByte(in);
				size += x;
			}
			return size;

		}
		else
		{
			return lengthCode;
		}
	}

}
