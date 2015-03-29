package com.perrier.music;

import com.perrier.music.config.Property;

public class ApplicationProperties {

	private ApplicationProperties() {
	}

	public static final Property<String> APP_ROOT = new Property<String>("appdir.root");
	public static final Property<String> COVERS_DIR = new Property<String>("appdir.covers");
	public static final Property<String> RESOURCES_DIR = new Property<String>("appdir.resources");
	public static final Property<String> DB_DIR = new Property<String>("appdir.db");
	public static final Property<String> LOG_DIR = new Property<String>("appdir.log");
	public static final Property<String> LOG_CONFIGFILE = new Property<String>("log.configfile");

	public static final Property<String> URL = new Property<String>("h2.url");
	public static final Property<String> USERNAME = new Property<String>("h2.username");
	public static final Property<String> PASSWORD = new Property<String>("h2.password");
}
