package com.github.msx80.kitteh;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.github.msx80.kitteh.impl.ConnectionImpl;
import com.github.msx80.kitteh.impl.RequestImpl;

public class RequestUtils 
{

	private static int indexOf(byte[] outerArray, byte[] smallerArray, int start) {
	    for(int i = start; i < outerArray.length - smallerArray.length+1; ++i) {
	        boolean found = true;
	        for(int j = 0; j < smallerArray.length; ++j) {
	           if (outerArray[i+j] != smallerArray[j]) {
	               found = false;
	               break;
	           }
	        }
	        if (found) return i;
	     }
	   return -1;  
	}  

	// from InputStream of java9
    private static byte[] readNBytes(InputStream is, int len) throws IOException {
        if (len < 0) {
            throw new IllegalArgumentException("len < 0");
        }

        List<byte[]> bufs = null;
        byte[] result = null;
        int total = 0;
        int remaining = len;
        int n;
        do {
            byte[] buf = new byte[Math.min(remaining, 8192)];
            int nread = 0;

            // read to EOF which may read more or less than buffer size
            while ((n = is.read(buf, nread,
                    Math.min(buf.length - nread, remaining))) > 0) {
                nread += n;
                remaining -= n;
            }

            if (nread > 0) {
                if ((Integer.MAX_VALUE - 8) - total < nread) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                if (nread < buf.length) {
                    buf = Arrays.copyOfRange(buf, 0, nread);
                }
                total += nread;
                if (result == null) {
                    result = buf;
                } else {
                    if (bufs == null) {
                        bufs = new ArrayList<>();
                        bufs.add(result);
                    }
                    bufs.add(buf);
                }
            }
            // if the last call to read returned -1 or the number of bytes
            // requested have been read then break
        } while (n >= 0 && remaining > 0);

        if (bufs == null) {
            if (result == null) {
                return new byte[0];
            }
            return result.length == total ?
                result : Arrays.copyOf(result, total);
        }

        result = new byte[total];
        int offset = 0;
        remaining = total;
        for (byte[] b : bufs) {
            int count = Math.min(b.length, remaining);
            System.arraycopy(b, 0, result, offset, count);
            offset += count;
            remaining -= count;
        }

        return result;
    }

	
	public static Request[] getMultipart(Request request) throws Exception
	{
		ArrayList<Request> res = new ArrayList<Request>();
		String req = request.getHeaders().get("content-type");
		if(req == null) throw new RuntimeException("No content type!");
		
		Header h = Header.parse(req);
		String boundary = h.getSub().get("boundary");
		if(!"multipart/form-data".equals(h.getCleanVal())) throw new RuntimeException("No multipart!");
		if(boundary == null) throw new RuntimeException("No boundary!");
		
		byte[] body = request.getBinaryBody();
		Files.write(Paths.get("bodyasis"), body);
		
		boundary = "--"+boundary;
		byte[] bound = boundary.getBytes(StandardCharsets.US_ASCII);
		

			byte[] xxx = new byte[bound.length];
			// ensure first boundary
			System.arraycopy(body, 0, xxx , 0, bound.length);
			if (Arrays.equals(body, bound))
			{
				throw new RuntimeException("Doesn't start with boundary");
			}
			
		
		int start = bound.length;
		while(true)
		{
			if (body[start] == '-' && body[start+1] == '-'  ) 
			{
				System.out.println("Finished!");
				break;
			}
			else if (body[start] == 13 && body[start+1] == 10  ) 
			{
				start += 2;
				
				int pos = indexOf(body, bound, start);
				if(pos == -1)
				{
					break;
				}
				byte[] bb = Arrays.copyOfRange(body, start, pos-2); // -2 to remove CRLF
				//var tmp = new String(bb, StandardCharsets.UTF_8);
				//System.out.println(tmp);
				BufferedInputStream bis = new BufferedInputStream( new ByteArrayInputStream(bb) );
				Map<String, String> headers = ConnectionImpl.loadReqData(bis);
				// var cd = headers.get("content-disposition");
				
				byte[] img = readNBytes(bis, Integer.MAX_VALUE);

				start = pos + bound.length;
				RequestImpl r = new RequestImpl();
				r.setHeaders(headers);
				r.setBinaryBody(img);
				
	            String contentTypeString = headers.getOrDefault("content-type", "");
	            Header ct = Header.parse(contentTypeString);
	            
	            String charset = ct.getSub().getOrDefault("charset", "ASCII");
	        	String bodyStr = new String(img, charset);
	        	r.setBody(bodyStr);
				
				res.add(r);
			}
			else
			{
				throw new RuntimeException("Unexpected bytes after boundary!");
			}
			
		}
		
		return res.toArray(new Request[res.size()]);
	}
	
	
}
