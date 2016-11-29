package com.perrier.music.api;

public class RestException extends Exception {

	public RestException(String msg) {
		super(msg);
	}

	public RestException(String msg, Throwable t) {
		super(msg, t);
	}
}
