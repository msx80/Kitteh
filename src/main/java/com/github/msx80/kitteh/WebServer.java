package com.github.msx80.kitteh;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.github.msx80.kitteh.impl.ConnectionImpl;
import com.github.msx80.kitteh.impl.RequestReader;

/**
 * The main Kitteh web server class.
 * 
 *
 */
public final class WebServer
{
    private ServerSocket serverSocket;
    private DocumentProducer producer;
    private WebSocketListener listener;
	private ExceptionHandler exceptionHandler;
	
	private Executor executor;
	
	private long maxBodySize;
	private int timeoutMillis;
	private boolean ownExecutor;
	
	private CompletableFuture<Void> terminated;
	
	protected WebServer(DocumentProducer producer, int port, InetAddress address , ServerSocket serverSocket, 
			Executor executor, ExceptionHandler exceptionHandler,  WebSocketListener listener, long maxBodySize, int timeoutMillis)
	{
		try {
			this.terminated = new CompletableFuture<>();
			this.producer = producer;
			this.ownExecutor = executor == null;
			if(ownExecutor)
			{
				executor = Executors.newCachedThreadPool(new ThreadFactory() {
					
					@Override
					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						t.setName("Kitteh Service Thread");
						t.setDaemon(true);
						return t;
					}
				});
			}
			this.executor = executor;
			this.exceptionHandler = exceptionHandler;
			this.listener = listener;
			this.maxBodySize = maxBodySize;
			this.timeoutMillis = timeoutMillis;
			if (serverSocket != null) {
				this.serverSocket = serverSocket;
			}
			else 
			{
				this.serverSocket = new ServerSocket(port, 50, address);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

    
    /**
     * Start the WebServer in a new thread. The method will return immediately
     * and the WebServer will work on its own thread.
     * @return The thread created for the WebServer
     */
    protected void runAsThread()
    {
    	executor.execute(new Runnable() 
    	{
			@Override
			public void run() {
				WebServer.this.runNow();
				
			}
		});
    }
    
    /**
     * Start the WebServer. This method will block indefinitely untill another thread
     * calls the close() method on this object. 
     */
    private void runNow()
    {
        try
        {
            while (true)
            {
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(timeoutMillis);
                ConnectionImpl c = new ConnectionImpl(socket, producer, listener, this.exceptionHandler, maxBodySize );
                
                RequestReader requestThread = new RequestReader(c);
                executor.execute(requestThread);
            }
        }
        catch (SocketException e)
        {
        	// socket closed
        }
        catch (IOException e)
        {
        	throw new RuntimeException(e);
        }
    }

    /**
     * Close this WebServer
     */
    public void close()
    {
    	try
    	{
    		// close the server socket
	        try
	        {
	            serverSocket.close();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        
	        // close the executor if it's ours
	        if(ownExecutor)
	        {
		        if(executor instanceof ExecutorService)
		        {
		        	try {
						((ExecutorService) executor).shutdown();
					} catch (Exception e) {
						e.printStackTrace();
					}
		        }
	        }
	        
    	}
    	finally
    	{
    		// wake waiting threads
    		terminated.complete(null);
    	}
        
    }
    
    public void waitTermination()
    {
    	try {
			terminated.get();
		} catch (Exception e) {
			// just give up
		}
    }
}