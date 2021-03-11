package examples;

import java.io.*;
import java.security.*;
import java.util.*;

import javax.net.ssl.*;

import com.github.msx80.kitteh.*;
import com.github.msx80.kitteh.producers.*;

import sun.security.jca.ProviderList;
import sun.security.jca.Providers;
import examples.pages.*;

/**
 * Just as the Main example, but with SSL!
 *
 */
public class MainSSL
{

	
	   private static SSLServerSocket getSecureServer(int port) throws Exception
	    {
	        // TODO: to generate key: keytool -genkey -keyalg RSA -keysize 2048 -alias ssltestserver -keystore ssltestserver.keys
		   // set password to mypass

	        SSLContext context = SSLContext.getInstance("SSLv3");
	        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
	        KeyStore ks = KeyStore.getInstance("JKS");

	        char[] password = "mypass".toCharArray();
	        ks.load(new FileInputStream("ssltestserver.keys"), password);
	        kmf.init(ks, password);
	        
	        context.init(kmf.getKeyManagers(), null, null);
	        
	        SSLServerSocketFactory factory = context.getServerSocketFactory();
	        
	        return (SSLServerSocket) factory.createServerSocket( port );        
	    }


	public static void main(String[] args) throws Exception
	{
		Map<String, Object> rules = new HashMap<String, Object>();
		
		rules.put("", new Welcome());
		rules.put("another\\.html", "examples.pages.Another");
		rules.put("slow\\.html", "examples.pages.Slow");
		rules.put("pages/(.*)", "examples.pages.dispatch.$1");
		rules.put("secret/(.*)",  new AuthenticationProducer(new Another(), "myuser", "mypass", "Top Secret Area"));
		rules.put("breakme", new DocumentProducer()
		{
			public void produceDocument(Request request, Response response) throws Exception, Redirection
			{
				System.out.println(request.getRemoteAddr());
				throw new Exception("You broke Kitteh!");
			}
		});
		DocumentProducer f = new FileProducer("www");
		
		DocumentProducer d = new DispatcherProducer(rules, f);
        int port = 8080;
		WebServer w = new WebServer(d,getSecureServer(port));
		w.runAsThread();
		System.out.println("Server started!");
		System.out.println("https://localhost:"+port+"/");
		System.out.println("Press ENTER to quit.");
		
		BufferedReader r = new BufferedReader(new InputStreamReader( System.in ));
		r.readLine();
		w.close();
		System.out.println("Quitting");

	}

}
