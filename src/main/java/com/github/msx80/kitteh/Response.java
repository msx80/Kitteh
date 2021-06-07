package com.github.msx80.kitteh;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;


public interface Response
{
	public Response setContent(byte[] content);
	public Response setContent(String content);
	public Response setContent(String content, Charset charset);
	public Response setContent(InputStream content) throws IOException;
	public Response setContent(InputStream content, int length) throws IOException;
	
	public String getContentType();
	public Response setContentType(String contentType);

	public Headers getHeaders();
	
	public int getHtmlReturnCode();
	public Response setHtmlReturnCode(int htmlReturnCode);
	
	public Response setCacheable(boolean cacheable);
	public boolean isCacheable();
	
}