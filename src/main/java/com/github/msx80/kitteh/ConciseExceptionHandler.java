package com.github.msx80.kitteh;

public class ConciseExceptionHandler implements ExceptionHandler {

	@Override
	public void handle(Exception e, Response response) {
		
        response.setHtmlReturnCode(500);
		//response.setContentType("text/plain");
        response.setContent("500 Internal server error");
    
	}

}
