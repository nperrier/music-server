package com.perrier.music.entity.album;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.DeleteQuery;

public class AlbumDeleteQuery extends DeleteQuery {

	private final Album album;

	public AlbumDeleteQuery(Album album) {
		this.album = album;
	}

	@Override
	public void query(Session session) throws DBException {
		session.delete(this.album);
	}
}
