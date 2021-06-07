package com.github.msx80.kitteh;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Headers 
{
	private Map<String, String> headers = new HashMap<String, String>();
	
	public Headers set(String header, String value) {
		headers.put(header.toLowerCase(), value);
		return this;
	}

	public String get(String header) {
		return headers.get(header.toLowerCase());
	}

	public Set<String> getKeys() {
		return headers.keySet();
	}

	public Map<String, String> unmodifiableMap() {
		return Collections.unmodifiableMap(headers);
	}

}
