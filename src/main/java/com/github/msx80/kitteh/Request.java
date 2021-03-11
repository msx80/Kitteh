package com.github.msx80.kitteh;

import java.util.*;

public interface Request
{
	/**
	 * Get the url that was used for this request, excluding the server address and the parameters
	 * That is: http://localhost:8080/test/index.html?param=1 will return test/index.html
	 * It's the same of getPathName()+'/'+getPageName()
	 * @return
	 */
	public String getDocumentName();
	
	/**
	 * Return the page name of the url. For example for http://localhost:8080/test/sub/index.html?param=1 it will return index.html
	 * @return
	 */
	public String getPageName();
	/**
	 * Return the path of the url without the name. For example for http://localhost:8080/test/sub/index.html?param=1 it will return test/sub
	 * @return
	 */
	public String getPathName();
	
	
	/**
	 * Returns a map of the parameters received by this request, either in the url with a GET or from the body in a POST with x-www-form-urlencoded 
	 * @return
	 */
	public Map<String, String> getParameters();
	
	/**
	 * Returns the http header of this request
	 * @return
	 */
	public Map<String, String> getHeaders();
	
	/**
	 * get the address of the client that made this request
	 * @return
	 */
	public String getRemoteAddr();

	/**
	 * returns a single parameter for the given name
	 * @param name
	 * @return
	 */
	public String getParameter(String name);
	
	/**
	 * get the current session
	 * @return
	 */
	public Map<String, Object> getSession();
	
	/**
	 * returns the unique identifier of the current session
	 * @return
	 */
	public String getSessionId();
	
	public Method getMethod();
	
	public String getBody();

	public void setLocalAttr(String key, Object val);
	public Object getLocalAttr(String key);
}