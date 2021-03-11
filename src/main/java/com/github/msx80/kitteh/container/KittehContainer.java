package com.github.msx80.kitteh.container;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;

import com.github.msx80.kitteh.DocumentProducer;
import com.github.msx80.kitteh.WebServer;

public class KittehContainer {

	public static void main(String[] args) throws Exception {
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
		
		
		WebServer w;
		if(localOnly)
		{
			w = new WebServer(kcd, port, InetAddress.getByName(null) /* listen on localhost only */ );
		}
		else
		{
			w = new WebServer(kcd, port);
		}
		
		w.run();
	}

	public static void parseLine(KittehContainerDispatcher kcd, String line)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if(line.contains(":"))
		{
			String[] tok = line.split(":");
			if(tok.length!=2) throw new IllegalArgumentException("Producer not in the form path:DocumentProducer");
			
			@SuppressWarnings("unchecked")
			Class<? extends DocumentProducer> cls = (Class<? extends DocumentProducer>) Class.forName(tok[1]);
			
			DocumentProducer xx = cls.newInstance();
			kcd.addProducer(tok[0], xx);
		}
		else
		{
			throw new IllegalArgumentException("Producer not in the form path:DocumentProducer");
		}
	}

}
