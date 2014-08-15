package com.perrier.music.entity.playlist;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class PlaylistFindAllQuery extends FindQuery<List<Playlist>> {

	// Eagerly load tracks (default is lazy)
	private final boolean includeTracks;

	PlaylistFindAllQuery(boolean includeTracks) {
		this.includeTracks = includeTracks;
	}

	@Override
	public List<Playlist> query(Session session) throws DBException {

		Criteria c = session.createCriteria(Playlist.class);

		if (this.includeTracks) {
			c.setFetchMode("playlistTracks", FetchMode.JOIN);
		}

		@SuppressWarnings("unchecked")
		List<Playlist> playlists = c.list();
		return playlists;
	}

}
