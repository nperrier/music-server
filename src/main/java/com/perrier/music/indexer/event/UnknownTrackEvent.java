package com.perrier.music.indexer.event;

import java.io.File;

import com.perrier.music.entity.library.Library;

public class UnknownTrackEvent implements ITrackEvent {

	private final File file;
	private final Library library;

	public UnknownTrackEvent(File file, Library library) {
		this.file = file;
		this.library = library;
	}

	public File getFile() {
		return file;
	}

	public Library getLibrary() {
		return library;
	}

	@Override
	public String toString() {
		return "UnknownTrackEvent{" + "file=" + file + ", library=" + library + '}';
	}
}
