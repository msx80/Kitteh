package com.github.msx80.kitteh.impl;

import java.io.IOException;
import java.util.concurrent.Executor;

public class RequestReader implements Runnable
{
	
    ConnectionImpl c;
    public RequestReader(ConnectionImpl c)
    {
        this.c = c;
    }
  
    public void run()
    {
    	try
    	{
			c.readRequest();
			c.writeResponse();
    	}
    	catch (Exception e) {
			try {
				c.closeSocket();
			} catch (IOException e1) {
				// no problem
			}
			return;
		}

    }

}