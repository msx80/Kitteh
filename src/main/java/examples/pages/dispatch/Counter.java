package examples.pages.dispatch;

import com.github.msx80.kitteh.*;

public class Counter implements DocumentProducer
{
	static int num = 0;
	
	public void produceDocument(Request request, Response response) throws Exception, Redirection
	{
		num++;
		response.setContent("<html><body><h1>count is now: "+num+"</h1></body></html>");
	}

}
