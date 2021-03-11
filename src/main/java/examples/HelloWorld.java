package examples;

import com.github.msx80.kitteh.*;

/**
 * This is the simplest, two line program you can do with Kitteh
 *
 */
public class HelloWorld implements DocumentProducer
{
	public void produceDocument(Request request, Response response)
	{
		response.setContent("<html><body><h1>Hello World!</h1></body></html>");
	}

	public static void main(String[] args) throws Exception
	{
		new WebServer( new HelloWorld(), 8080).run();
	}
}
