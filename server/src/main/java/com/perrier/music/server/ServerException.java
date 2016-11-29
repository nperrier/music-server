package com.perrier.music.server;

public class ServerException extends Exception {

	private static final long serialVersionUID = -5841893874726461821L;

	public ServerException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
