package com.github.msx80.kitteh.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.msx80.kitteh.Headers;
import com.github.msx80.kitteh.Response;

public class ResponseImpl implements Response
{
 
	private int htmlReturnCode = 200;
	private InputStream content = null;
	private int contentLength = 0;
	private String contentType;
    private boolean cacheable = false;
	private Headers headers = new Headers();
    
    
	
	public ResponseImpl()
	{
		this.contentType = "text/html; charset=utf-8";
	}
	
	@Override
	public int getHtmlReturnCode()
	{
		return htmlReturnCode;
	}
	
	@Override
	public Response setHtmlReturnCode(int htmlReturnCode)
	{
		this.htmlReturnCode = htmlReturnCode;
		return this;
	}

	
	public InputStream getContent()
	{
		return content;
	}
	
	@Override
	public Response setContent(String content)
	{
		byte[] arr = content.getBytes();
		this.content = new ByteArrayInputStream(arr);
		this.contentLength = arr.length;
		return this;
	}
	
	@Override
	public Response setContent(String content, Charset charset)
	{
		byte[] arr = content.getBytes(charset);
		this.content = new ByteArrayInputStream(arr);
		this.contentLength = arr.length;
		return this;
	}
	
	@Override
	public Response setContent(InputStream content) throws IOException
	{
		this.content = content;
		this.contentLength = -1;
		return this;
	}
	
	@Override
	public Response setContent(InputStream content, int length) throws IOException 
	{
		this.content = content;
		this.contentLength = length;
		return this;
	}
	
	@Override
	public Response setContent(byte[] content)
	{	
		this.content = new ByteArrayInputStream(content);
		this.contentLength = content.length;
		return this;
	}

	@Override
	public String getContentType()
	{
		return contentType;
	}

    
	@Override
	public boolean isCacheable()
	{
		return cacheable;
	}

	@Override
	public Response setCacheable(boolean cacheable)
	{
		this.cacheable = cacheable;
		return this;
	}

	public int getContentLength() {
		return contentLength;
	}


	@Override
	public Response setContentType(String contentType) {
			
		this.contentType = contentType;;
		return this;
	}

	@Override
	public Headers getHeaders() {
		return headers ;
	}



	
}
