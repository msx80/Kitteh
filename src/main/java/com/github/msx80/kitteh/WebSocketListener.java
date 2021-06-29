package com.github.msx80.kitteh;

/**
 * Interface that defines how to interact with a WebSocket
 * 
 */
public interface WebSocketListener 
{
	/**
	 * Notification that a new WebSocket has connected.
	 * @param ws the WebSocket that connected
	 */
	public void connection(WebSocket ws);
	
	/**
	 * Called when a Text message arrives.
	 * @param ws the socket that generated the message
	 * @param data the message
	 */
	
	public void dataText(WebSocket ws, String data);
	/**
	 * Called when a Binary message arrive.
	 * @param ws
	 * @param data
	 */
	public void dataBinary(WebSocket ws, byte[] data);
	
	/**
	 * Called when a certain websocket disconnects. Use this to cleanup your data but don't send any more message to the socket.
	 * @param ws
	 */
	public void disconnection(WebSocket ws);

}
