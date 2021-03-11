package com.github.msx80.kitteh.producers.annotations;

import com.github.msx80.kitteh.Response;

public interface ResultHandler<E> {
	void handleResult(E result, Response response) throws Exception;
}
