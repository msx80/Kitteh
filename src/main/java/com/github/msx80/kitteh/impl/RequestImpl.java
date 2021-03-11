package com.github.msx80.kitteh.impl;

import java.util.HashMap;
import java.util.Map;

import com.github.msx80.kitteh.*;

public class RequestImpl implements Request 
{
	private String documentName;
	private Map<String, String> parameters = new HashMap<String, String>();
	private Map<String, String> headers = new HashMap<String, String>();
	private Map<String, Object> localAttr = new HashMap<String, Object>();
	private Map<String, Object> session = null;
	private String sessionId = null;
	private String remoteAddr = null;
	private Method method = null;
	private String body;
	private String pageName;
	private String pathName;
	
	
	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public Map<String, Object> getSession()
	{
		return session;
	}
	
	public String getDocumentName() 
	{
		return documentName;
	}
	
	public void setSession(Map<String, Object> session)
	{
		this.session = session;
	}

	public Map<String, String> getParameters() 
	{
		return parameters;
	}

	public Map<String, String> getHeaders() 
	{
		return headers;
	}
	
	public String getRemoteAddr()
	{
		return remoteAddr;
	}

	public void setDocumentName(String documentName)
	{
		this.documentName = documentName;
		int n = documentName.lastIndexOf('/');
		if(n == -1)
		{
			pathName = "";
			pageName = documentName;
		}
		else
		{
			pathName = documentName.substring(0, n);
			pageName = documentName.substring(n+1);
		}
	}

	public void setParameters(Map<String, String> parameters)
	{
		this.parameters = parameters;
	}

	public void setHeaders(Map<String, String> headers)
	{
		this.headers = headers;
	}

	public void setRemoteAddr(String remoteAddr)
	{
		this.remoteAddr = remoteAddr;
	}

	
	public String getParameter(String name)
	{
		return (String)parameters.get(name);
	}

	public Method getMethod()
	{
		return method ;
	}
	public void setMethod(Method m)
	{
		method = m;
	}
	
	

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
		
	}

	@Override
	public void setLocalAttr(String key, Object val) {
		localAttr.put(key, val);
		
	}

	@Override
	public Object getLocalAttr(String key) {
		return localAttr.get(key);
	}

	@Override
	public String getPageName() {
		
		return pageName;
	}

	@Override
	public String getPathName() {
		return pathName;
	}
}
