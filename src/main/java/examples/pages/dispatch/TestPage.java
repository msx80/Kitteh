package examples.pages.dispatch;

import com.github.msx80.kitteh.*;

public class TestPage implements DocumentProducer
{

	public void produceDocument(Request request, Response response) throws Exception, Redirection
	{
		response.setContent("<html><body><h1>Test Page!</h1></body></html>");
	}

}
