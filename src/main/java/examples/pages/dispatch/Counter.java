package examples.pages.dispatch;

import java.util.concurrent.atomic.AtomicInteger;

import com.github.msx80.kitteh.*;

public class Counter implements DocumentProducer
{
	static AtomicInteger num = new AtomicInteger(0);
	
	public void produceDocument(Request request, Response response) throws Exception, Redirection
	{
		int i = num.incrementAndGet();
		response.setContent("<html><body><h1>count is now: "+i+"</h1></body></html>");
	}

}
