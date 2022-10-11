package com.github.msx80.kitteh.impl;

import java.util.HashMap;
import java.util.Map;

public class ContentType {

	final String contentType;
	final Map<String, String> parameters;
	
	public ContentType(String contentType, Map<String, String> parameters) {
		super();
		this.contentType = contentType;
		this.parameters = parameters;
	}

	public static ContentType parse(String contentTypeString) {
		String[] toks = contentTypeString.split(";");
		Map<String, String> params = new HashMap<String, String>();
		for (int i = 1; i < toks.length; i++) {
			parseParam(toks[i], params);
		}
		return new ContentType(toks[0].trim(), params);
	}

	private static void parseParam(String param, Map<String, String> out) {
		
		String[] toks = param.split("=");
		if (toks.length!=2) {
			throw new RuntimeException("Malformed content type parameter");
		}
		out.put(toks[0].trim().toLowerCase(), toks[1].trim());
	}

	@Override
	public String toString() {
		return "ContentType [contentType=" + contentType + ", parameters=" + parameters + "]";
	}

	public static void main(String[] args)
	{
		System.out.println(ContentType.parse("application/x-www-form-urlencoded; charset=UTF-8"));
		System.out.println(ContentType.parse("application/x-www-form-urlencoded ; charset=UTF-8"));
		System.out.println(ContentType.parse("application/x-www-form-urlencoded"));
		System.out.println(ContentType.parse("application/x-www-form-urlencoded ;"));
		System.out.println(ContentType.parse("application/x-www-form-urlencoded;pippo=pluto;pippo=pluto;pippo=pluto"));
		
	}
}
