package examples;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.github.msx80.kitteh.DocumentProducer;
import com.github.msx80.kitteh.Redirection;
import com.github.msx80.kitteh.Request;
import com.github.msx80.kitteh.Response;
import com.github.msx80.kitteh.WebServer;
import com.github.msx80.kitteh.WebServerBuilder;
import com.github.msx80.kitteh.producers.AuthenticationProducer;
import com.github.msx80.kitteh.producers.DispatcherProducer;
import com.github.msx80.kitteh.producers.FileProducer;
import com.github.msx80.kitteh.producers.LoggingProducer;

import examples.pages.Informations;
import examples.pages.Welcome;

/**
 * An example showing some features of Kitteh
 *
 */
public class Main
{
	
	public static void main(String[] args) throws Exception
	{
        int port = 8080;
        WebServer w = WebServerBuilder
			.produce(getProducer())
			.port(port)
			.run();
        
		System.out.println("Server started!");
		System.out.println("http://localhost:"+port+"/");
		System.out.println("Press ENTER to quit.");
		
		BufferedReader r = new BufferedReader(new InputStreamReader( System.in ));
		r.readLine();
		w.close();
		System.out.println("Quitting");
	}

	public static DocumentProducer getProducer() throws Exception {
		Map<String, Object> rules = new HashMap<String, Object>();
		
		rules.put("", new Welcome());
		rules.put("another\\.html", "examples.pages.Informations");
		rules.put("slow\\.html", "examples.pages.Slow");
		rules.put("pages/(.*)", "examples.pages.dispatch.$1");
		rules.put("secret/(.*)",  new AuthenticationProducer(new Informations(), "myuser", "mypass", "Top Secret Area"));
		rules.put("breakme", new DocumentProducer()
		{
			public void produceDocument(Request request, Response response) throws Exception, Redirection
			{
				System.out.println(request.getRemoteAddr());
				throw new Exception("You broke Kitteh!");
			}
		});
		DocumentProducer f = new FileProducer("www");
		
		DocumentProducer d = new LoggingProducer( new DispatcherProducer(rules, f) );
		return d;
	}
	
  
    


}
