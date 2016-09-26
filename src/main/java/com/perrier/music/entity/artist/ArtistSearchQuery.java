package com.perrier.music.entity.artist;

import java.util.List;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class ArtistSearchQuery extends FindQuery<List<Artist>> {

	public ArtistSearchQuery(String query) {
		super();
	}

	@Override
	public List<Artist> query(Session session) throws DBException {
		return null;
	}
}
