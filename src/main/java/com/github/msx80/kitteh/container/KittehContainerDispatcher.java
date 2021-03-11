package com.github.msx80.kitteh.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.msx80.kitteh.DocumentProducer;
import com.github.msx80.kitteh.Redirection;
import com.github.msx80.kitteh.Request;
import com.github.msx80.kitteh.Response;
import com.github.msx80.kitteh.utils.Cleaner;

public class KittehContainerDispatcher implements DocumentProducer {

	private Map<String, DocumentProducer> producers = new HashMap<>();
	private DocumentProducer fallBack = DocumentProducer.ERR_404_PRODUCER;
	
	
	
	public KittehContainerDispatcher() {
		super();
	}
	
	public KittehContainerDispatcher(DocumentProducer fallBack) {
		super();
		this.fallBack = fallBack;
	}
	public void addProducer(final String path, final DocumentProducer c)
	{
		String rep = Cleaner.cleanDocName(path);
		
		
		if(producers.containsKey(rep)) throw new IllegalArgumentException("The path requested by this container is already associated to another container");
		producers.put(rep, c);
	}
	
	
	
	public void removeProducer(DocumentProducer c)
	{
		for (Map.Entry<String, DocumentProducer> e : producers.entrySet()) {
			if(e.getValue().equals(c))
			{
				producers.remove(e.getKey());
				return;
			}
		}
	}
	
	@Override
	public void produceDocument(Request request, Response response) throws Exception, Redirection {
		
		String pathx = request.getDocumentName();
		for (Entry<String, DocumentProducer> e : producers.entrySet()) {
			String pathy = e.getKey();
			
			if(pathx.startsWith(pathy))
			{
				e.getValue().produceDocument(request, response);
				return;
			}
					
		}
		/*
		DocumentProducer byPath = producers.get(request.getPathName());
		if(byPath!=null)
		{
			byPath.produceDocument(request, response);
			return;
		}
		*/
		
		
		fallBack.produceDocument(request, response);
	}

}
