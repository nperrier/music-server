package com.perrier.music.entity.track;

import java.util.List;

import com.google.inject.Inject;
import com.perrier.music.api.TrackUpdateDto;
import com.perrier.music.db.DBException;
import com.perrier.music.db.IDatabase;
import com.perrier.music.entity.update.TrackUpdater;

public class TrackProvider {

	private final IDatabase db;
	private final TrackUpdater trackUpdater;

	@Inject
	public TrackProvider(IDatabase db, TrackUpdater trackUpdater) {
		this.db = db;
		this.trackUpdater = trackUpdater;
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

	public List<Track> findAllByLibraryId(Long id) throws DBException {
		List<Track> tracks = this.db.find(new TrackFindAllByLibraryIdQuery(id));
		return tracks;
	}

	public List<Track> findAllByGenreId(Long id) throws DBException {
		List<Track> tracks = this.db.find(new TracksFindAllByGenreIdQuery(id));
		return tracks;
	}

	// TODO limit fetch size
	public List<Track> findAll() throws DBException {
		List<Track> tracks = this.db.find(new TrackFindAllQuery());
		return tracks;
	}

	public Track update(Track track, TrackUpdateDto trackUpdateDto) throws DBException {
		Track updatedTrack = this.trackUpdater.handleUpdates(track, trackUpdateDto);
		return updatedTrack;
	}

	public List<Track> findRandom() throws DBException {
		List<Track> tracks = this.db.find(new TrackFindRandomQuery());
		return tracks;
	}
}
