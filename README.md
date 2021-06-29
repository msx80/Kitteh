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
* HTML basic authentication
* basic SSL support
* Basic Websocket support
* etc.

Available in MAVEN via [JitPack](https://jitpack.io/#msx80/kitteh).

How to use Kitteh
==================

You create and run an instance of WebServer via the WebServerBuilder facility, passing a single [DocumentProducer](https://github.com/msx80/Kitteh/blob/main/src/main/java/com/github/msx80/kitteh/DocumentProducer.java), which is a very simple interface where you define what your server will serve:

    void produceDocument(Request request, Response response) 
        

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

Many utility `DocumentProducer` are included [here](https://github.com/msx80/Kitteh/tree/main/src/main/java/com/github/msx80/kitteh/producers), to implement various things like HTML Authentication, serving files or dispatching, either via rules or with annotations:

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
		
		public static void main(String[] args) throws IOException
		{
			WebServerBuilder
				.produce(new AnnotationProducer(new AnnotatedProducer()))
				.port( 8080)
				.run()
				.waitTermination();
		}
	}

Here's a dispatcher based on rules. You can map to either instances of other DocumentProducers or to class names, where regex pattern matching is available:

		Map<String, Object> rules = new HashMap<String, Object>();
		
		rules.put("", new Welcome());
		rules.put("another\\.html", "examples.pages.Informations");
		rules.put("slow\\.html", "examples.pages.Slow");
		rules.put("pages/(.*)", "examples.pages.dispatch.$1");
		rules.put("secret/(.*)",  new AuthenticationProducer(new Informations(), "myuser", "mypass", "Top Secret Area"));
		DocumentProducer f = new FileProducer("www");
		
		DocumentProducer d = new DispatcherProducer(rules, f);
		
DocumentProducers can be composed with the decorator pattern to add many functionalities, such as logging, concurrency, etc.