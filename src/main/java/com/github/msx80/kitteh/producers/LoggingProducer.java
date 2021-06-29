package com.github.msx80.kitteh.producers;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.msx80.kitteh.DocumentProducer;
import com.github.msx80.kitteh.Redirection;
import com.github.msx80.kitteh.Request;
import com.github.msx80.kitteh.Response;

public class LoggingProducer implements DocumentProducer {

	private static final Logger LOG = Logger.getLogger( LoggingProducer.class.getName() );
	
	DocumentProducer src;
	
	
	
	public LoggingProducer(DocumentProducer src) {
		super();
		this.src = src;
	}



	@Override
	public void produceDocument(Request request, Response response) throws Exception, Redirection {
		
		LOG.info("Request from "+request.getRemoteAddr()+": "+request.getDocumentName());
		try
		{
			src.produceDocument(request, response);
			LOG.info("Request from "+request.getRemoteAddr()+": "+request.getDocumentName()+" Completed");
		}
		catch (Exception e) {
			LOG.log(Level.SEVERE, "Request from "+request.getRemoteAddr()+": "+request.getDocumentName()+" ERROR:"+e.getMessage(), e);
		}
		
	}

}
