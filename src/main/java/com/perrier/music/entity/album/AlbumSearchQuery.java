package com.perrier.music.entity.album;

import java.util.List;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class AlbumSearchQuery extends FindQuery<List<Album>> {

	public AlbumSearchQuery(String query) {
		super();
	}

	@Override
	public List<Album> query(Session session) throws DBException {
		return null;
	}
}
