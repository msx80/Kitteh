package com.github.msx80.kitteh.producers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.github.msx80.kitteh.DocumentProducer;
import com.github.msx80.kitteh.Redirection;
import com.github.msx80.kitteh.Request;
import com.github.msx80.kitteh.Response;
import com.github.msx80.kitteh.WebServerBuilder;

/**
 * A producer that delegate production of response to a different thread or context.
 * Useful to generate pages inside concurrent enviromnents, like in event queues etc.
 * You provide a consumer of Runnable and a source producer.
 * You run the runnable at a later time on the appropriate context, doing that will call the src
 * producer. Only response generation is run in the context, all network transmission happens on Kitteh 
 * threads.
 */
public class DeferredProducer implements DocumentProducer {

	Consumer<Runnable> exec;
	DocumentProducer src;
	
	
	/**
	 * 
	 * @param exec
	 * @param src
	 */
	public DeferredProducer(Consumer<Runnable> exec, DocumentProducer src) {
		super();
		this.exec = exec;
		this.src = src;
	}



	@Override
	public void produceDocument(Request request, Response response) throws Exception, Redirection {
		
		CompletableFuture<Void> c = new CompletableFuture<>();
		exec.accept(() -> {
			try {
				src.produceDocument(request, response);
			} catch (Throwable e) {
				c.completeExceptionally(e);
				return;
			}
			c.complete(null);
			
		});
		
		try {
			c.get();
		} catch (ExecutionException e) {
			Throwable ex = e.getCause();
			if(ex instanceof Exception) throw (Exception)ex;
			if(ex instanceof Redirection) throw (Redirection)ex;
			throw e;
		}

	}

}
