package com.perrier.music.config;

public class OptionalProperty<T> extends Property<T> {
	
	private final T defaultValue;
	
	public OptionalProperty(String key, T defaultValue) {
		super(key);
		this.defaultValue = defaultValue;
	}
	
	public T getDefaultValue() {
		return this.defaultValue;
	}
}