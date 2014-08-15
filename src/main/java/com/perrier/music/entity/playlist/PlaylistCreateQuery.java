package com.perrier.music.entity.playlist;

import org.hibernate.Session;

import com.perrier.music.db.CreateQuery;
import com.perrier.music.db.DBException;

public class PlaylistCreateQuery extends CreateQuery<Playlist> {

	private final Playlist playlist;

	public PlaylistCreateQuery(Playlist playlist) {
		this.playlist = playlist;
	}

	@Override
	public Playlist query(Session session) throws DBException {
		session.save(this.playlist);
		return this.playlist;
	}
}
