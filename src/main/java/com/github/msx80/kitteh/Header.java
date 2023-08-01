package com.github.msx80.kitteh;

import java.util.HashMap;
import java.util.Map;

public class Header {

	private String cleanVal;
	private Map<String, String> sub;

	public Header(String cleanVal, Map<String, String> sub) {
		this.cleanVal = cleanVal;
		this.sub = sub;
	}

	public String getCleanVal() {
		return cleanVal;
	}

	public Map<String, String> getSub() {
		return sub;
	}

	@Override
	public String toString() {
		return "Header [cleanVal=" + cleanVal + ", sub=" + sub + "]";
	}

	public static Header parse(String value)
	{
		String[] tok = value.split(";");
		String cleanVal = tok[0].trim();
		
		Map<String, String> sub = new HashMap<>();
		for (int i = 1; i < tok.length; i++) {
			String a = tok[i];
			int n = a.indexOf('=');
			String k = a.substring(0, n).trim().toLowerCase();
			String v = a.substring(n+1).trim();
			sub.put(k, v);
		}
		
		return new Header(cleanVal, sub);
	}
	
}
