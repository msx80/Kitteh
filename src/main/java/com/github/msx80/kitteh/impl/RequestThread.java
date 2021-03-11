package com.github.msx80.kitteh.impl;

import com.github.msx80.kitteh.*;

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
		
    }

}