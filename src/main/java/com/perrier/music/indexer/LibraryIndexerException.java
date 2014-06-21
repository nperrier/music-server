package com.perrier.music.indexer;

public class LibraryIndexerException extends Exception {

	private static final long serialVersionUID = -6335894176056706363L;

	public LibraryIndexerException(String msg) {
		super(msg);
	}
	
	public LibraryIndexerException(String msg, Throwable t) {
		super(msg, t);
	}
}
