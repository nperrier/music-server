package com.perrier.music.entity.playlist;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.UpdateQuery;

public class PlaylistUpdateQuery extends UpdateQuery<Playlist> {

	private final Playlist playlist;

	public PlaylistUpdateQuery(Playlist playlist) {
		this.playlist = playlist;
	}

	@Override
	public Playlist query(Session session) throws DBException {
		session.update(this.playlist);
		return this.playlist;
	}
}
