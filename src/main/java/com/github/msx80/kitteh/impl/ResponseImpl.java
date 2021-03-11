package com.github.msx80.kitteh.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.github.msx80.kitteh.Response;

public class ResponseImpl implements Response
{
 
	private int htmlReturnCode = 200;
	private InputStream content = null;
	private int contentLength = 0;
	private String contentType;
    private boolean cacheable = false;
    
    private Map<String, String> headers = new HashMap<String, String>();
	
	public ResponseImpl()
	{
		this.contentType = "text/html";
	}
	
	public int getHtmlReturnCode()
	{
		return htmlReturnCode;
	}

	public void setHtmlReturnCode(int htmlReturnCode)
	{
		this.htmlReturnCode = htmlReturnCode;
	}

	public InputStream getContent()
	{
		return content;
	}
	public void setContent(String content)
	{
		byte[] arr = content.getBytes();
		this.content = new ByteArrayInputStream(arr);
		this.contentLength = arr.length;
	}
	
	public void setContent(String content, Charset charset)
	{
		byte[] arr = content.getBytes(charset);
		this.content = new ByteArrayInputStream(arr);
		this.contentLength = arr.length;
	}
	
	public void setContent(InputStream content) throws IOException
	{
		this.content = content;
		this.contentLength = -1;
	}
	public void setContent(InputStream content, int length) throws IOException 
	{
		this.content = content;
		this.contentLength = length;
	}
	
	public void setContent(byte[] content)
	{	
		this.content = new ByteArrayInputStream(content);
		this.contentLength = content.length;
	}

	public String getContentType()
	{
		return contentType;
	}

	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}
		
    public Map<String, String> getHeaders()
    {
        return headers;
    }

	public boolean isCacheable()
	{
		return cacheable;
	}

	public void setCacheable(boolean cacheable)
	{
		this.cacheable = cacheable;
	}

	public int getContentLength() {
		return contentLength;
	}

	
}
