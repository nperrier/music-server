package com.perrier.music.entity.album;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.UpdateQuery;

public class AlbumUpdateQuery extends UpdateQuery<Album> {

	private final Album album;

	public AlbumUpdateQuery(Album album) {
		this.album = album;
	}

	@Override
	public Album query(Session session) throws DBException {
		session.update(album);
		return album;
	}
}
