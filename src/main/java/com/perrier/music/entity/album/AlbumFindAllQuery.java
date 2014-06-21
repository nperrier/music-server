package com.perrier.music.entity.album;

import java.util.List;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class AlbumFindAllQuery extends FindQuery<List<Album>> {

	@Override
	public List<Album> query(Session session) throws DBException {
		@SuppressWarnings("unchecked")
		List<Album> albums = session.createCriteria(Album.class).list();
		return albums;
	}

}
