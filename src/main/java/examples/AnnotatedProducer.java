package examples;

import java.io.IOException;

import com.github.msx80.kitteh.Response;
import com.github.msx80.kitteh.WebServer;
import com.github.msx80.kitteh.producers.AnnotationProducer;
import com.github.msx80.kitteh.producers.annotations.Get;
import com.github.msx80.kitteh.producers.annotations.NamedArg;
import com.github.msx80.kitteh.producers.annotations.Post;

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
		new WebServer( new AnnotationProducer(new AnnotatedProducer()), 8080).run();
	}
}
