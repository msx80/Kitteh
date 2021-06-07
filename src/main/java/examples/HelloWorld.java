package examples;

import com.github.msx80.kitteh.*;

/**
 * This is the simplest program you can do with Kitteh
 *
 */
public class HelloWorld 
{
	public static void main(String[] args) throws Exception
	{
		String html = "<html><body><h1>Hello World!</h1></body></html>";
		WebServerBuilder
			.produce( (req, res) -> res.setContent(html) )
			.port(8080)
			.run()
			.waitTermination();

	}
}
