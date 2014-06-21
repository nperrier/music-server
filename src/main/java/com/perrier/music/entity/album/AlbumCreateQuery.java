package com.perrier.music.entity.album;

import org.hibernate.Session;

import com.perrier.music.db.CreateQuery;
import com.perrier.music.db.DBException;

public class AlbumCreateQuery extends CreateQuery<Album> {

	private final Album album;

	public AlbumCreateQuery(Album album) {
		this.album = album;
	}

	@Override
	public Album query(Session session) throws DBException {
		session.save(this.album);
		return this.album;
	}
}
