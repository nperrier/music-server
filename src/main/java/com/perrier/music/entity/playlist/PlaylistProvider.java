package com.perrier.music.entity.playlist;

import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;

import com.perrier.music.db.DBException;
import com.perrier.music.db.IDatabase;
import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.album.AlbumFindByIdQuery;
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

	public void delete(Playlist playlist) throws DBException {
		this.db.delete(new PlaylistDeleteQuery(playlist));
	}

	public void addAlbumToPlaylist(Playlist playlist, Long albumId, Integer position) throws DBException {

		this.db.beginTransaction();
		try {

			Album album = this.db.find(new AlbumFindByIdQuery(albumId));
			final List<Track> albumTracks = album.getTracks();
			// sort by number
			Collections.sort(albumTracks, (t1, t2) -> {
				if (t1.getNumber() > t2.getNumber()) {
					return 1;
				} else if (t1.getNumber() < t2.getNumber()) {
					return -1;
				}
				return 0;
			});

			this.db.create(new PlaylistAddTrackQuery(playlist, albumTracks, position));

			this.db.commit();

		} finally {
			this.db.endTransaction();
		}
	}

	public void addTracksToPlaylist(Playlist playlist, List<Long> trackIds, Integer position) throws DBException {

		this.db.beginTransaction();
		try {

			List<Track> tracks = this.db.find(new TrackFindByIds(trackIds));
			// TODO: the above query returns tracks in random order
			// we want to add the tracks to the playlist in the order of trackIds
			this.db.create(new PlaylistAddTrackQuery(playlist, tracks, position));

			this.db.commit();

		} finally {
			this.db.endTransaction();
		}
	}
}
