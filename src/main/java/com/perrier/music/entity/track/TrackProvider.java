package com.perrier.music.entity.track;

import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;
import com.perrier.music.db.DBException;
import com.perrier.music.db.IDatabase;

public class TrackProvider {

	private final IDatabase db;

	@Inject
	public TrackProvider(IDatabase db) {
		this.db = db;
	}

	public Track findById(long id) throws DBException {
		Track track = this.db.find(new TrackFindByIdQuery(id));
		return track;
	}

	public void create(Track track) throws DBException {
		this.db.create(new TrackCreateQuery(track));
	}

	public List<Track> findAllByArtistId(Long id) throws DBException {
		List<Track> tracks = this.db.find(new TracksFindAllByArtistIdQuery(id));
		return tracks;
	}

	public List<Track> findAllByAlbumId(Long id) throws DBException {
		List<Track> tracks = this.db.find(new TracksFindAllByAlbumIdQuery(id));
		return tracks;
	}

	public List<Track> findAllByLibraryId(Long id) {
		return Collections.emptyList();
	}

	// TODO limit fetch size
	public List<Track> findAll() throws DBException {
		List<Track> tracks = this.db.find(new TrackFindAllQuery());
		return tracks;
	}
}
