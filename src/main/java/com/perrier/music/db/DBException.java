package com.perrier.music.db;

public class DBException extends Exception {

	private static final long serialVersionUID = 235174298935083120L;

	public DBException(String string) {
		super(string);
	}

	public DBException(String string, Throwable e) {
		super(string, e);
	}

}
