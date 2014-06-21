package com.perrier.music;

import com.perrier.music.config.Property;

public class ApplicationProperties {

	public static final Property<String> APP_ROOT = new Property<String>("appdir.root");
	public static final Property<String> COVERS_DIR = new Property<String>("appdir.covers");
	public static final Property<String> RESOURCES_DIR = new Property<String>("appdir.resources");
}
