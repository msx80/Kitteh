package com.github.msx80.kitteh;

public interface WebSocket {

	public void sendString(String data);
	
	public void sendBinary(byte[] data);

	public Request getRequest();

	public void close();
	
	public Object getUserData();
	public void setUserData(Object data);
}
