package com.github.msx80.kitteh.impl;

import com.github.msx80.kitteh.ConnectionProcessor;

public class RequestThread extends Thread
{
    ConnectionImpl c;
    ConnectionProcessor cp;
    public RequestThread(ConnectionImpl c, ConnectionProcessor cp)
    {
        this.c = c;
        this.cp = cp;
        setDaemon(true);
        setName("Kitteh Client");
    }
  
    public void run()
    {
    	//long start = System.currentTimeMillis();
		//System.out.println("Started serving connection");
		c.readRequest();
		if(cp == null)
		{
		    // if not separated process, process ourself
		    c.run();
		}
		else
		{
		    // else delegate to processor
		    cp.processConnection(c);
		}
		//System.out.println("Finished serving request in "+(System.currentTimeMillis()-start)+" millis.");
    }

}