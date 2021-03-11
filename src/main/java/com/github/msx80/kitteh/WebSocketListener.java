package com.github.msx80.kitteh;

public interface WebSocketListener 
{
	public void connection(WebSocket ws);
	public void dataText(WebSocket ws, String data);
	public void dataBinary(WebSocket ws, byte[] data);
	public void disconnection(WebSocket ws);

}
