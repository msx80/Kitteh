package com.github.msx80.kitteh.impl.ws;

public class ConnectionClosedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3030450619869263233L;

	public ConnectionClosedException() {
	
	}

	public ConnectionClosedException(String message) {
		super(message);
		
	}

	public ConnectionClosedException(Throwable cause) {
		super(cause);
	}

	public ConnectionClosedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConnectionClosedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
