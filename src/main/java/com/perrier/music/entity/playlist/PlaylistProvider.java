package com.perrier.music.entity.playlist;

import java.util.List;

import com.google.inject.Inject;
import com.perrier.music.db.DBException;
import com.perrier.music.db.IDatabase;
import com.perrier.music.entity.track.Track;
import com.perrier.music.entity.track.TrackFindByIds;

public class PlaylistProvider {

	private final IDatabase db;

	@Inject
	public PlaylistProvider(IDatabase db) {
		this.db = db;
	}

	public List<Playlist> findAll(boolean includeTracks) throws DBException {
		List<Playlist> playlists = this.db.find(new PlaylistFindAllQuery(includeTracks));
		return playlists;
	}

	public Playlist findById(long id, boolean includeTracks) throws DBException {
		Playlist playlist = this.db.find(new PlaylistFindByIdQuery(id, includeTracks));
		return playlist;
	}

	public void create(Playlist playlist) throws DBException {
		this.db.create(new PlaylistCreateQuery(playlist));
	}

	public void addTracksToPlaylist(Playlist playlist, List<Long> trackIds, Integer position) throws DBException {

		this.db.beginTransaction();
		try {

			List<Track> tracks = this.db.find(new TrackFindByIds(trackIds));

			this.db.create(new PlaylistAddTrackQuery(playlist, tracks, position));

			this.db.commit();

		} finally {
			this.db.endTransaction();
		}
	}
}
