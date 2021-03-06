package com.perrier.music;

import java.util.Collections;
import java.util.List;

import com.perrier.music.config.OptionalProperty;
import com.perrier.music.config.Property;

/**
 * Common application-wide properties
 */
public class ApplicationProperties {

	private ApplicationProperties() {
	}

	public static final Property<String> APP_ROOT = new Property<>("appdir.root");
	public static final Property<String> COVERS_DIR = new Property<>("appdir.covers");
	public static final Property<String> RESOURCES_DIR = new Property<>("appdir.resources");
	public static final Property<String> LOG_DIR = new Property<>("appdir.log");
	public static final Property<String> LOG_CONFIGFILE = new Property<>("log.configfile");
	public static final Property<Integer> PORT = new Property<>("server.port");

	public static final Property<String> URL = new Property<>("db.url");
	public static final Property<String> USERNAME = new Property<>("db.username");
	public static final Property<String> PASSWORD = new Property<>("db.password");

	public static final OptionalProperty<List> MIME_TYPES = new OptionalProperty<>("server.mimetypes",
			Collections.emptyList());

	public static final Property<String> AWS_ACCESS_KEY_ID = new Property<>("aws.accesskeyid");
	public static final Property<String> AWS_SECRET_ACCESS_KEY = new Property<>("aws.secretaccesskey");
	public static final Property<String> AWS_BUCKET = new Property<>("aws.bucket");
	public static final Property<String> AWS_REGION = new Property<>("aws.region");

}
