Kitteh, a lightweight pure java webserver
==========================================

Kitteh is a very simple web server. It's not J2EE compilant, it's a 
standalone library that you can include in your existing project to
implement a web user interface.

Features:
* very small footprint
* sessions
* dispatching
* parameters and headers parsing
* serving static or dynamic content
* SSL support
* HTML basic authentication
* etc.

How to use Kitteh
==================

The following is the smallest Kitteh2 program. Other more complex examples 
are present in the "examples" package.

public class HelloWorld implements DocumentProducer
{
	public void produceDocument(Request request, Response response)
	{
		String s = "<html><body><h1>Hello World!</h1></body></html>";
		response.setContent(s);
	}

	public static void main(String[] args) throws Exception
	{
		new WebServer( new HelloWorld(), 8080).run();
	}
}


