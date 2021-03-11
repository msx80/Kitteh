package com.github.msx80.kitteh.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import com.github.msx80.kitteh.DocumentProducer;
import com.github.msx80.kitteh.Method;
import com.github.msx80.kitteh.Redirection;
import com.github.msx80.kitteh.WebSocketListener;
import com.github.msx80.kitteh.impl.ws.WebSocketImpl;
import com.github.msx80.kitteh.utils.AlmostInfiniteByteArray;
import com.github.msx80.kitteh.utils.Cleaner;
import com.github.msx80.kitteh.utils.StreamUtils;

public class ConnectionImpl implements Runnable
{
	private static final Charset utf = Charset.forName("UTF-8");
    private static final String COOKIE_NAME = "session-cookie";
	
    private Socket socket;
    private Random rand = new SecureRandom();

    private DocumentProducer pageProducer;
    
    RequestImpl req = new RequestImpl(); 
    ResponseImpl resp = new ResponseImpl();

	private WebSocketListener wsHandler;
    
    
    public ConnectionImpl(Socket socket, DocumentProducer pageProducer, WebSocketListener wsHandler)
    {
        this.socket = socket;
        this.pageProducer = pageProducer;
        this.wsHandler = wsHandler;
       
    }

    private static String readLine(BufferedInputStream is, Charset cs) throws IOException
    {
    	AlmostInfiniteByteArray b = new AlmostInfiniteByteArray(1024); 
    	int c; 
    	 
    	do
    	{
    	   c= is.read(); 
    	   if(c== '\r')
    	   {
    		   is.mark(1);
    		   if(is.read()!='\n')
    		   {
    			   is.reset();
    		   }
    		   break; 
    	   }
    	   if(c== -1)
     	      break; 
    	   b.append((byte)c);
    	}while(true);
    	
    	return b.encodeAsString(cs);
    }
    
    private static Map<String, String> stringToParams(String p) throws UnsupportedEncodingException
    {
        HashMap<String, String> res = new HashMap<String, String>();
        StringTokenizer t = new StringTokenizer(p, "&");
        while (t.hasMoreTokens())
        {
            String couple = t.nextToken();
            int pos = couple.indexOf("=");
            if (pos == -1)
            {
                res.put(couple, "");
            }
            else
            {
                String key = couple.substring(0, pos);
                String val = couple.substring(pos + 1, couple.length());

                val = URLDecoder.decode(val, "UTF-8");
               
                res.put(key, val);
            }

        }
        return res;
    }

    private static void sendHeader(OutputStream out, int code,
            String contentType, long contentLength, boolean cacheable,
            String location, Map<String, String> headers) throws IOException
    {
        StringBuffer h = new StringBuffer();

        h.append("HTTP/1.0 ");
        h.append(code);
        h.append(" OK\r\n");
        h.append("Date: ");
        h.append(new Date().toString());
        h.append("\r\n");
        h.append("Server: WebServer/1.0\r\n");
        h.append("Connection: close\r\n");
        
        if(contentLength == 0l)
        {
        	
        }
        else
        {
	        h.append("Content-Type: ");
	        h.append(contentType);
	        h.append("\r\n");
	        
	        if (cacheable)
	        {
	        	headers.put("Expires", "Thu, 01 Jan 2100 00:00:00 GMT");
	        }
	        else
	        {
	        	headers.put("Cache-Control","no-cache");
	        	headers.put("Pragma","no-cache");
	        	headers.put("Expires", "Thu, 01 Jan 1970 00:00:00 GMT");
	        	/*h.append("Last-modified: ");
	            h.append(new Date().toString());
	            h.append("\r\n");*/
			}
        }
        
        if (contentLength > 0)
        {
            h.append("Content-Length: ");
            h.append(contentLength);
            h.append("\r\n");
        }
        
        if (location != null)
        {
            h.append("Location: " + location + "\r\n");
        }
        

        for (String header : headers.keySet())
        {
            h.append(header+": "+headers.get(header));
            h.append("\r\n");
        }

        
        h.append("\r\n");
        out.write(h.toString().getBytes());
    }

    private static void error(OutputStream out, Exception e) throws IOException
    {
    	StringWriter s = new StringWriter();
    	PrintWriter pw = new PrintWriter(s);
    	 
        pw.println("Kitteh Server error");
        pw.println("-------------------");
        e.printStackTrace(pw);
        pw.close();
        byte[] buf = s.getBuffer().toString().getBytes();
        sendHeader(out, 500, "text/plain", buf.length, false, null, new HashMap<String, String>(0));
        out.write(buf);
    }

    private Map<String, String> loadReqData(BufferedInputStream in) throws IOException
    {
        HashMap<String, String> x = new HashMap<String, String>();
        String s = readLine(in, utf);
        while (!"".equals(s))
        {
            String[] couple = s.split(":", 2);
            if (couple.length == 2)
            {
                x.put(couple[0].trim().toLowerCase(), couple[1].trim());
            }
            s = readLine(in, utf);

        }
        return x;

    }

    private String getCookie(Map<String, String> headers)
    {
    	String c = (String)headers.get("cookie");
    	if (c != null)
		{
	    	String[] x = c.split("=");
	    	if (x[0].equals(COOKIE_NAME))
			{
				return x[1];
			}
		}
    	return null;
           
    }
    private void handleRequest(RequestImpl request, BufferedInputStream in) throws IOException
    {
        // read the first line. HelloWorld "GET /index.html HTTP/1.1"
        String firstLine = readLine(in, utf);
        if (firstLine == null)
        {
            throw new IOException("Null request");
        }
        // separate the three token
        String[] req = firstLine.split(" ", 3);
        if (req.length != 3)
        {
        	System.out.println(req);
            throw new IOException("Non standard request");
        }
        // method and document name.. forget the protocol :P
        String method = req[0].toLowerCase();
        String docName = req[1];
        
        // load request data
        request.setHeaders( loadReqData(in) );

        if (method.equals("post"))
        {
        	request.setMethod(Method.POST);
            // if it is post, read the following data..
        	request.setDocumentName( Cleaner.cleanDocName(docName) );
        	
            int size = Integer.parseInt((String) request.getHeaders().get("content-length"));
            byte[] b = new byte[size];
            
            StreamUtils.readFully(in, b);
           
            String body = new String(b, utf);
            if(request.getHeaders().get("content-type").equalsIgnoreCase("application/x-www-form-urlencoded"))
            {
            	request.setParameters( stringToParams(body) );
            }
            request.setBody(body);
        } 
        else
        {	
        	Method mm = Method.valueOf(method.toUpperCase());
	        if (mm != null)
	        {
	        	request.setMethod(mm);
	            // if it is GET, separate the parameters from the doc name
	            int pos = docName.indexOf("?");
	            if (pos == -1)
	            {
	                // no parameters passed
	                request.setDocumentName( Cleaner.cleanDocName(docName ) );
	            }
	            else
	            {
	                // parameters!
	            	request.setDocumentName( Cleaner.cleanDocName(docName.substring(0, pos)) );
	                String paramStr = docName.substring(pos + 1, docName.length());
	                request.setParameters( stringToParams(paramStr) );
	            }
	
	        }
	        else
	        {
	            throw new IOException("Unknown method");
	        }
        }
        
    }

	

    private String bakeNewCookie()
    {
    	StringBuffer s = new StringBuffer();
    	while (s.length()<30)
		{
			s.append( Long.toString( rand.nextLong() & Long.MAX_VALUE , 36 ).toUpperCase() );
		}
    	return s.substring(0, 30);

    }
    private static int copyStream(InputStream in, OutputStream out) throws IOException 
	  {
		  int bufferSize = 1024;
		  int streamLength = 0;
		  byte[] buffer = new byte[bufferSize];
		  for (int len=in.read(buffer); len>0; len=in.read(buffer) ) 
		  {
			  out.write(buffer, 0, len);
			  out.flush();
			  streamLength += len;
		  }
		  return streamLength;
	  }
    private void writeResponse() throws IOException
    {
		OutputStream out = socket.getOutputStream();
		try
		{
	        	pageProducer.produceDocument(req, resp);
	        	sendHeader(out, resp.getHtmlReturnCode(), resp.getContentType(), resp.getContentLength(), resp.isCacheable(), null, resp.getHeaders());
	        	InputStream cont = resp.getContent();
	        	if(cont == null) throw new NullPointerException("Response content is null");
	        	try
	        	{
	        		copyStream(cont, out);
	        	}
	        	finally
	        	{
	        		cont.close();
	        	}
		}
		catch (Redirection e)
		{
		    // handle user redirections
		    String d = "Document moved";
		    sendHeader(out, 302, "text/plain", d.length(), false, e.getUrl(), new HashMap<String, String>(0));
		    out.write(d.getBytes());
		}
		catch (Exception e)
		{
		    // send generic unexpected exception
		    error(out, e);
		}
		out.flush();
    }

    public void readRequest()
    {
		try
		{
		    socket.setSoTimeout(600000);
		    
		    BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
		    
		    handleRequest(req, in);
		    
		    req.setRemoteAddr( socket.getInetAddress().getHostAddress().toString()+":"+socket.getPort() );
		    
		    handleSession(req, resp);
		}
		catch (Exception e)
		{
		    try
		    {
		    	socket.close();
		    }
		    catch (IOException e1)
		    {
		    	// nothing
		    }
		    e.printStackTrace();
		}
    }

    private void handleSession(RequestImpl req, ResponseImpl resp)
    {
		String cookie = getCookie(req.getHeaders());
		if ((cookie == null) || (!SessionContainer.hasSession(cookie)))
		{
			cookie = bakeNewCookie();
			resp.getHeaders().put("Set-Cookie", COOKIE_NAME+"="+cookie);
		}
		req.setSession(SessionContainer.getSession(cookie));
		req.setSessionId(cookie);
    }

    public void run()
    {
		try
		{
			if(isWebSocketRequest())
			{
				WebSocketImpl ws = new WebSocketImpl(socket, req, wsHandler);
				ws.start();				
			}
			else
			{
				try
				{
					writeResponse();
				}
				finally
				{
					try
				    {
				    	socket.close();
				    }
				    catch (IOException e1)
				    {
				    	// nothing
				    }
				}
			}
		}
		catch (IOException e)
		{
		    
		    e.printStackTrace();
		}
		
		
    }




	private boolean isWebSocketRequest() {
		String con = req.getHeaders().get("connection");
		String upg = req.getHeaders().get("upgrade");
		
		return "Upgrade".equalsIgnoreCase(con) && "websocket".equalsIgnoreCase(upg);
	}
}
