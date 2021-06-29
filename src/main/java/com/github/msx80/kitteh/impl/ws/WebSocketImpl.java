package com.github.msx80.kitteh.impl.ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;

import com.github.msx80.kitteh.Request;
import com.github.msx80.kitteh.WebSocket;
import com.github.msx80.kitteh.WebSocketListener;

public class WebSocketImpl implements WebSocket{
	
	private Socket socket;
	private Request request;
	private WebSocketListener listener;
	private OutputStream out;
	private InputStream in;
	private Object data;
	private Executor executor;

	public WebSocketImpl(Socket socket, Request request, WebSocketListener listener, Executor ex) 
	{
		this.socket = socket;
		this.request = request;
		this.listener = listener;
		this.executor = ex;
		
	}

	@Override
	public synchronized void sendString(String data) {
		
		try {
			FrameReader.sendText(out, data);
		
		} catch (IOException e) {
			
			e.printStackTrace();
			// ensure socket is close to wake up thread
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(e);
		}
	}

	@Override
	public synchronized void sendBinary(byte[] data) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public Request getRequest() {
		
		return request;
	}

	
	private String createResponseKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException 
	{
		byte[] magic = (key+"258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("ASCII");
		
		MessageDigest m = MessageDigest.getInstance("SHA1");

		m.update(magic);
		
		byte[] res = m.digest();
		String response = Base64.encodeBytes(res);
		return response;
	}
	
	private void handleWebSocket()  {
		boolean connectionNotified = false;
		try {

			out = socket.getOutputStream();
			in = socket.getInputStream();
			
			sendHandshakeResponse();
		                
			connectionNotified = true; // set before actual notification so if the listener throws, we still call disconnect on them.
		    listener.connection(this);
		    
		    while(true)
	        {
	        	Object data = FrameReader.readFrame(in);
	        	if(data == null)
	        	{
	        		socket.close();
	        		
	        		break;
	        	}
	        	else if (data instanceof String)
	        	{
	        		listener.dataText(this, (String) data);
	        	}
	        	else if (data instanceof byte[])
	        	{
	        		listener.dataBinary(this, (byte[]) data);
	        	}
	        }
		    
		}
		catch (SocketException e) 
		{
			System.err.println(e.toString());
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				if(connectionNotified) listener.disconnection(this);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			// ensure we always close the socket 
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	private void sendHandshakeResponse() throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException 
	{
		String key = request.getHeaders().get("sec-websocket-key");
		String responseKey = createResponseKey(key);			
		StringBuffer h = new StringBuffer();

	    h.append("HTTP/1.1 101 Switching Protocols\r\n");
	    h.append("Upgrade: websocket\r\n");
	    h.append("Connection: Upgrade\r\n");
	    h.append("Sec-WebSocket-Accept: ");
	    h.append(responseKey);
	    h.append("\r\n");
	    h.append("\r\n");
	    out.write(h.toString().getBytes(FrameReader.UTF8));
	    out.flush();
	}

	public void start() {
		executor.execute(this::handleWebSocket);
		
	}

	@Override
	public int hashCode() {
		return this.request.getSessionId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		
		return obj == this;
	}

	@Override
	public void close() {
		try {
			// this should break the reading thread and send the disconnect message
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public Object getUserData() {
		
		return data;
	}

	@Override
	public void setUserData(Object data) {
		this.data = data;
		
	}

	
}
