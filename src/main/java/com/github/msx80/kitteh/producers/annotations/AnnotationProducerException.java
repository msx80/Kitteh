package com.github.msx80.kitteh.producers.annotations;

public class AnnotationProducerException extends RuntimeException {


	private static final long serialVersionUID = -1657832988583963133L;

	public AnnotationProducerException() {
		super();
	}

	public AnnotationProducerException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AnnotationProducerException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnnotationProducerException(String message) {
		super(message);
	}

	public AnnotationProducerException(Throwable cause) {
		super(cause);
	}

}
