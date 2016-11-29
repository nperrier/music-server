package com.perrier.music.entity.playlist;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.DeleteQuery;

public class PlaylistDeleteQuery extends DeleteQuery {

	private final Playlist playlist;

	public PlaylistDeleteQuery(Playlist playlist) {
		this.playlist = playlist;
	}

	@Override
	public void query(Session session) throws DBException {
		session.delete(this.playlist);
	}
}
