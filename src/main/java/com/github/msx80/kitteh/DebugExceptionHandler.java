package com.github.msx80.kitteh;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DebugExceptionHandler implements ExceptionHandler {

	@Override
	public void handle(Exception e, Response response) {
		
	
    	StringWriter s = new StringWriter();
    	PrintWriter pw = new PrintWriter(s);
    	 
        pw.println("Kitteh Server error");
        pw.println("-------------------");
        e.printStackTrace(pw);
        pw.close();

        response.setHtmlReturnCode(500);
		response.setContentType("text/plain");
        response.setContent(s.getBuffer().toString());
        		
	}

}
