package com.github.msx80.kitteh.producers;

import com.github.msx80.kitteh.Request;

public interface Authenticator {
	public boolean validate(Request request, String user, String password);
}
