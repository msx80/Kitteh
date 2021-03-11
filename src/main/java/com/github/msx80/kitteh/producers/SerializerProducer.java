package com.github.msx80.kitteh.producers;

import com.github.msx80.kitteh.*;

/**
 * This producer serialize all requests by synchronizing on a lock,
 * so that requests are executed one at a time and never concurrently.
 * This is useful if you have a multithreaded application with restrictions
 * on multiple access to data. 
 * The lock is managed automatically or can be supplied by the user to 
 * synchronize correctly on the application logic. 
 *
 */
public class SerializerProducer implements DocumentProducer
{
	private DocumentProducer producer;
	private Object lock;
	
	public SerializerProducer(DocumentProducer producer)
	{
		super();
		this.producer = producer;
		this.lock = new Object();
	}
	
	public SerializerProducer(DocumentProducer producer, Object lock)
	{
		super();
		this.producer = producer;
		this.lock = lock;
	}

	public void produceDocument(Request request, Response response) throws Exception, Redirection
	{
		synchronized (lock)
		{
			producer.produceDocument(request, response);
		}

	}

}
