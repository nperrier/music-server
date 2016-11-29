package com.perrier.music.entity.album;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class AlbumFindByIdQuery extends FindQuery<Album> {

	private final long id;

	public AlbumFindByIdQuery(long id) {
		this.id = id;
	}

	@Override
	public Album query(Session session) throws DBException {
		Album album = (Album) session.get(Album.class, this.id);
		return album;
	}
}
