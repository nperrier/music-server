package com.perrier.music.coverart;

public class CoverArtException extends Exception {

	private static final long serialVersionUID = -6183659325215304102L;

	public CoverArtException(String msg) {
		super(msg);
	}
	
	public CoverArtException(String msg, Throwable t) {
		super(msg, t);
	}
}
