package com.github.msx80.kitteh;

import java.io.*;
import java.net.*;

import com.github.msx80.kitteh.impl.*;

/**
 * The main Kitteh web server class.
 * 
 *
 */
public class WebServer implements Runnable
{
    private ServerSocket serverSocket;
    private DocumentProducer producer;
    private ConnectionProcessor connectionProcessor = null;
    private WebSocketListener listener;
	private ExceptionHandler exceptionHandler = new ConciseExceptionHandler();
    /**
     * Create a new WebServer
     * @param producer the object to which the requests will be passed
     * @param port a port to listen to
     * @throws Exception
     */
    public WebServer(DocumentProducer producer, int port) throws IOException
    {
    	this.producer = producer;
        serverSocket = new ServerSocket(port, 50);
    }

    /**
     * Create a new WebServer that will be bound to the specified address.
     * @param producer the object to which the requests will be passed
     * @param port a port to listen to
     * @param address an address to listen on
     * @throws IOException
     */
    public WebServer(DocumentProducer producer, int port, InetAddress address) throws IOException
    {
    	this.producer = producer;
    	serverSocket = new ServerSocket(port, 50, address);
    }
    
    /**
     * Create a new WebServer that will use the supplied, already bound socket to listen for connections.
     * @param producer the object to which the requests will be passed
     * @param serverSocket any server socket to accept connections
     * @throws Exception
     */
    public WebServer(DocumentProducer producer, ServerSocket serverSocket)
    {
    	this.producer = producer;
        this.serverSocket = serverSocket;
    }
    
    /**
     * Start the WebServer in a new thread. The method will return immediately
     * and the WebServer will work on its own thread.
     * @return The thread created for the WebServer
     */
    public Thread runAsThread()
    {
    	Thread t = new Thread(this, "Kitteh Server");
    	t.setDaemon(true);
    	t.start();
    	return t;
    }
    
    /**
     * Start the WebServer. This method will block indefinitely untill another thread
     * calls the close() method on this object. 
     */
    public void run()
    {
        try
        {
            while (true)
            {
                Socket socket = serverSocket.accept();
                ConnectionImpl c = new ConnectionImpl(socket, producer, listener, this.exceptionHandler );
                
                RequestThread requestThread = new RequestThread(c, connectionProcessor);
                requestThread.start();
                
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
            serverSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Set a new producer for this WebServer
     * @param p a new producer to use
     */
    public void setProducer(DocumentProducer p)
    {
        producer = p;
    }

    /**
     * Set a custom ConnectionProcessor as the current processor.
     * This will be called to defer request responding to a later time/thread.
     * If the current processor is null, connections are processed immediately from within 
     * their own thread. Else, processing is delayed until you call Connection.process().
     * @param c you can pass null
     */
   public void setConnectionProcessor(ConnectionProcessor c)
   {
       connectionProcessor = c;
   }

	public WebSocketListener getWebSocketListener() {
		return listener;
	}

	public void setWebSocketListener(WebSocketListener listener) {
		this.listener = listener;
	}

	public ExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

   
    

    
}