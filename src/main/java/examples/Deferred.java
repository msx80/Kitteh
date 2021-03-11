package examples;

import java.io.*;
import java.util.concurrent.*;

import com.github.msx80.kitteh.*;

public class Deferred implements DocumentProducer, ConnectionProcessor, Runnable
{
	BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(5);

	public void produceDocument(Request request, Response response)
	{
		// here we are safe because we are doing stuff in our thread
		// we can safely access data structures of our application
		String threadName = Thread.currentThread().getName();
		response.setContent("<html><body><h1>Called from thread: " + threadName	+ "</h1></body></html>");
	}

	public void processConnection(Runnable connection)
	{
		// this is called from the client request thread, not the main thread.
		String threadName = Thread.currentThread().getName();
		System.out.println("Called from thread: " + threadName);

		// this method doesn't need to be synchronized because
		// we only enqueue the connection. No logic on this method!
		try
		{
			// don't process now, just put in the queue
			queue.put(connection);
		} 
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}

	}

	public Deferred() throws Exception
	{
		// setup the server
		WebServer w = new WebServer(this, 8080);
		w.setConnectionProcessor(this);
		w.runAsThread();

		// launch our processor
		Thread t = new Thread(this);
		t.setName("PROCESSOR THREAD");
		t.setDaemon(true);
		t.start();

		// now we just wait..
		System.out.println("Server started!");
		System.out.println("Press ENTER to quit.");

		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		r.readLine();
		w.close();
		System.out.println("Quitting");

	}

	public static void main(String[] args) throws Exception
	{
		new Deferred();
	}

	public void run()
	{
		// suppose this is an event loop of a bigger framework
		while (true)
		{

			try
			{
				// take connection from the queue and process them.
				Runnable connection = queue.take();
				connection.run();
			} 
			catch (InterruptedException e)
			{
				// closing!
				return;
			}

		}

	}

}
