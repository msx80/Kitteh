package com.github.msx80.kitteh.container;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLClassLoader;

import com.github.msx80.kitteh.DocumentProducer;
import com.github.msx80.kitteh.WebServerBuilder;

/**
 * Utility to run a single kitteh dispatching to multiple stuff, so you can create a single instance with multiple jars or something
 *
 */
public class KittehContainer {

	public static void main(String[] args) throws Exception 
	{
		if(args.length!=1) throw new RuntimeException("please supply exacly one parameter: the configuration file.");
		String configFile = args[0];
		File config = new File(configFile);
		System.out.println("Reading conf from "+config.getAbsolutePath());
		KittehContainerDispatcher kcd = new KittehContainerDispatcher();
		int port = 80;
		boolean localOnly = false;
		try(BufferedReader r = new BufferedReader( new FileReader(config) ) )
		{
			String line;
			while ((line = r.readLine()) != null)
			{
				line = line.trim();
				if(line.startsWith("#") || line.isEmpty()) continue;
				
				String[] tok = line.split("=");
				if(tok.length!=2) throw new IllegalArgumentException("Line not in the form key=value");
				if(tok[0].equals("port"))
				{
					port = Integer.parseInt(tok[1]);
				}
				else if(tok[0].equals("producer"))
				{
					parseLine(kcd, tok[1]);
				}
				else if(tok[0].equals("localOnly"))
				{
					localOnly = tok[1].equals("true");
				}
				else
				{
					throw new IllegalArgumentException("Unknown parameter "+tok[0]);
				}
			}
		}
		
		
		WebServerBuilder b = WebServerBuilder.produce(kcd).port(port);
		if(localOnly)
		{
			// listen on localhost only 
			b.bindTo(InetAddress.getByName(null) );
		}
	
		b.run().waitTermination();
	}

	@SuppressWarnings("unchecked")
	public static void parseLine(KittehContainerDispatcher kcd, String line) throws Exception {
		if(line.contains(":"))
		{
			String[] tok = line.split(":");
			if(tok.length!=3) throw new IllegalArgumentException("Producer not in the form path:jar:DocumentProducer");
			
			Class<? extends DocumentProducer> cls;
			if(tok[1].equals("null"))
			{
				cls = (Class<? extends DocumentProducer>) Class.forName(tok[2]);
			}
			else
			{
				URLClassLoader u = new URLClassLoader(new URL[] { new File(tok[1]).toURI().toURL() });
				cls = (Class<? extends DocumentProducer>) u.loadClass(tok[2]);
			}
			
			DocumentProducer xx = cls.newInstance();
			kcd.addProducer(tok[0], xx);
		}
		else
		{
			throw new IllegalArgumentException("Producer not in the form path:DocumentProducer");
		}
	}

}
