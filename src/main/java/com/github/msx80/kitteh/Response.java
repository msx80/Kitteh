package com.github.msx80.kitteh;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;


public interface Response
{
	public void setContent(byte[] content);
	public void setContent(String content);
	public void setContent(String content, Charset charset);
	public void setContent(InputStream content) throws IOException;
	public void setContent(InputStream content, int length) throws IOException;
	
	public String getContentType();
	public void setContentType(String contentType);
	
    public Map<String, String> getHeaders();
	public int getHtmlReturnCode();
	public void setHtmlReturnCode(int htmlReturnCode);
	
	public void setCacheable(boolean cacheable);
	public boolean isCacheable();
	
}