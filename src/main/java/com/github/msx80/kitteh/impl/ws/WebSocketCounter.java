package com.github.msx80.kitteh.impl.ws;

import java.util.concurrent.atomic.AtomicInteger;

import com.github.msx80.kitteh.WebSocket;
import com.github.msx80.kitteh.WebSocketListener;

/** just a test */
public class WebSocketCounter implements WebSocketListener {

	AtomicInteger count = new AtomicInteger(0);
	WebSocketListener src;
	
	public WebSocketCounter(WebSocketListener src) {
		this.src = src;
	}

	@Override
	public void connection(WebSocket ws) {
		int i = count.incrementAndGet();
		src.connection(ws);
		System.out.println("Websocket opened, there are now "+i+" websockets");
	}

	@Override
	public void dataText(WebSocket ws, String data) {
		src.dataText(ws, data);

	}

	@Override
	public void dataBinary(WebSocket ws, byte[] data) {
		src.dataBinary(ws, data);
	}

	@Override
	public void disconnection(WebSocket ws) {
		src.disconnection(ws);
		int i = count.decrementAndGet();
		System.out.println("Websocket closed, there are now "+i+" websockets");
	}

}
