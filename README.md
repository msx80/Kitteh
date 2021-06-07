[![Release](https://jitpack.io/v/msx80/kitteh.svg)](https://jitpack.io/#msx80/kitteh)

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
* basic SSL support
* HTML basic authentication
* etc.

How to use Kitteh
==================

The following is the smallest Kitteh2 program. Other more complex examples 
are present in the "examples" package.

	public static void main(String[] args) throws Exception
	{
		String html = "<html><body><h1>Hello World!</h1></body></html>";
		WebServerBuilder
			.produce( (req, res) -> res.setContent(html) )
			.port(8080)
			.run()
			.waitTermination();
	}

Or with annotations:

    public class AnnotatedProducer {
	
		@Get public void hello(Response response, @NamedArg("name") String name)
		{
			response.setContent("Greetings to: "+name);
		}
		
		@Get public String hello2(@NamedArg("name") String name)
		{
			return "More greetings to: "+name;
		}
		
		@Get public String sum(@NamedArg("a") Integer a, @NamedArg("b") Integer b)
		{
			return "Sum is: " + (a+b);
		}
		
		@Post public String onlypost()
		{
			return "Called via POST";
		}
		
		
		public static void main(String[] args) throws IOException
		{
			WebServerBuilder
				.produce(new AnnotationProducer(new AnnotatedProducer()))
				.port( 8080)
				.run()
				.waitTermination();
		}
	}
