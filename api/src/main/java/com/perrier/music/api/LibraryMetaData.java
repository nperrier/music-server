package com.perrier.music.api;

import java.util.Collections;
import java.util.List;

public class LibraryMetaData {

	private List<TrackMetaData> trackMetaData = Collections.emptyList();

	public LibraryMetaData() {
	}

	public void setTrackMetaData(List<TrackMetaData> trackMetaData) {
		this.trackMetaData = trackMetaData;
	}

	public List<TrackMetaData> getTrackMetaData() {
		return trackMetaData;
	}
}
