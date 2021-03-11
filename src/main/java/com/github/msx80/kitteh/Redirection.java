package com.github.msx80.kitteh;

public class Redirection extends Throwable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5153952397273334660L;
	private String url;

	public Redirection(String url)
	{
		super();
		this.url = url;
	}

	public String getUrl()
	{
		return url;
	}

}
