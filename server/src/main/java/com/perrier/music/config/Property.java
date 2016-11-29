package com.perrier.music.config;

public class Property<T> {

	private final String key;
	
	public Property(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return this.key;
	}
}