package com.github.msx80.kitteh;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WebServerBuilder {

	private int port = 8080;
	private Executor executor;
	private DocumentProducer producer;
	private InetAddress address;
	private ServerSocket serverSocket;
	private long maxBodySize;
	private WebSocketListener webSocketListener;
	private ExceptionHandler exceptionHandler = new ConciseExceptionHandler();
	private int timeoutMillis = 5000;
	
	public WebServerBuilder port(int port) {
		this.port = port;
		return this;
	}

	public WebServerBuilder bindTo(InetAddress address) {
		this.address = address;
		return this;
	}
	public WebServerBuilder serverSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
		return this;
	}	
	public WebServerBuilder timeoutMillis(int timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
		return this;
	}
	public WebServerBuilder maxBodySize(long maxBodySize) {
		this.maxBodySize = maxBodySize;
		return this;
	}
	public WebServerBuilder webSocketListener(WebSocketListener webSocketListener) {
		this.webSocketListener = webSocketListener;
		return this;
	}
	
	public WebServerBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
		return this;
	}
	
	/**
	 * Instantiate the Builder
	 * @return
	 */
	public static WebServerBuilder produce(DocumentProducer p)
	{
		WebServerBuilder w = new WebServerBuilder();
		w.producer = p;
		return w;
	}
	
	/**
	 * set a custom Executor that will be used to process incoming connections. If a custom executor is set, it will not be automatically closed/shutdown so it is responsability of the caller to handle closing.
	 * @param ex
	 * @return
	 */
	public WebServerBuilder executor(Executor ex)
	{
		this.executor = ex;
		return this;
	}
	
	/**
	 * Create the webserver with the selected options 
	 * @return
	 */
	private WebServer build()
	{
		
		return new WebServer(producer, port, address, serverSocket, executor, exceptionHandler, 
				webSocketListener, maxBodySize, timeoutMillis);
	}
	
	
	
	public WebServer run()
	{
		WebServer w = build();
		w.runAsThread();
		return w;
	}
	
	
	
}
