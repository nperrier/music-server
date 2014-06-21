package com.perrier.music.indexer.event;

import com.perrier.music.entity.library.Library;
import com.perrier.music.entity.track.Track;

public class MissingTrackEvent implements ITrackEvent {

	private final Track track;
	private final Library library;
	
	public MissingTrackEvent(Track track, Library library) {
		this.track = track;
		this.library = library;
	}

	public Library getLibrary() {
		return library;
	}

	public Track getTrack() {
		return track;
	}
}
