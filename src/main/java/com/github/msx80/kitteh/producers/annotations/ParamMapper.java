package com.github.msx80.kitteh.producers.annotations;

public interface ParamMapper<T> {

	T parseString(String val);
	
}
