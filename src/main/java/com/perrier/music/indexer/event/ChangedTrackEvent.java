package com.perrier.music.indexer.event;

import java.io.File;

import com.perrier.music.entity.library.Library;
import com.perrier.music.entity.track.Track;

public class ChangedTrackEvent implements ITrackEvent {

	private final File file;
	private final Track track;
	private final Library library;

	public ChangedTrackEvent(File file, Track track, Library library) {
		this.file = file;
		this.track = track;
		this.library = library;
	}

	public File getFile() {
		return this.file;
	}

	public Track getTrack() {
		return track;
	}

	public Library getLibrary() {
		return library;
	}

	@Override
	public String toString() {
		return "ChangedTrackEvent{" + "file=" + file + ", track=" + track + ", library=" + library + '}';
	}
}
