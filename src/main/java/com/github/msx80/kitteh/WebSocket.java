package com.github.msx80.kitteh;

public interface WebSocket {

	/**
	 * Send some text to the server via this socket
	 * @param data
	 */
	public void sendString(String data);
	
	/**
	 * Actually unimplemented
	 * @param data
	 */
	public void sendBinary(byte[] data);

	/**
	 * Get the HTTP request associated with this socket (to inspect the query string or address etc) 
	 */
	public Request getRequest();

	/**
	 * Close this websocket. A "disconnection" event will be received by the listener
	 */
	public void close();
	
	/**
	 * Retrieve user data set to this websocket
	 * @return
	 */
	public Object getUserData();
	
	/**
	 * Attach some user defined data to this socket. Use to associate any data structure to this socket and retrieve it efficiently.
	 * Data can be retrieved for the whole duration of the socket.
	 * @param data any data you wish to associate to this socket.
	 */
	public void setUserData(Object data);
}
