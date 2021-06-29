package com.github.msx80.kitteh.producers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.github.msx80.kitteh.DocumentProducer;
import com.github.msx80.kitteh.Mime;
import com.github.msx80.kitteh.Redirection;
import com.github.msx80.kitteh.Request;
import com.github.msx80.kitteh.Response;

/**
 * Produce responses based on resources included in classpath
 * @deprecated this is probably unsafe to use as is, i have to check
 */

public class ResourceProducer implements DocumentProducer {


	private Class<?> resourceClass;
	private String basePath;
	private DocumentProducer fallback;
	
	public ResourceProducer(Class<?> resourceClass, String basePath, DocumentProducer fallback) throws Exception
	{
		this.resourceClass = resourceClass;
		this.basePath = basePath;
		this.fallback = fallback;
	}
	
	public ResourceProducer(Class<?> resourceClass, String basePath) throws Exception
	{
		this(resourceClass, basePath, DocumentProducer.ERR_404_PRODUCER);
	}
	
	public void produceDocument(Request request, Response response) throws Exception, Redirection
	{
		String name = basePath+"/"+request.getDocumentName();
		InputStream is = resourceClass.getResourceAsStream(name);
		
		if (is == null)
		{
			
			fallback.produceDocument(request, response);
			
		}
		else
		{
			sendFile(response, is, name);
		}
	}
	private void sendFile(Response response, InputStream is, String name) throws FileNotFoundException, IOException
	{
		response
			.setCacheable(true)
			.setContent( is )
			.setContentType( Mime.getMIMEType(new File(name)) );
	}

}
