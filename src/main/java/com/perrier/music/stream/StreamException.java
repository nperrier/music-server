package com.perrier.music.stream;

public class StreamException extends Exception {

	private static final long serialVersionUID = 8726592959152855280L;

	public StreamException(String msg, Throwable t) {
		super(msg, t);
	}
}
