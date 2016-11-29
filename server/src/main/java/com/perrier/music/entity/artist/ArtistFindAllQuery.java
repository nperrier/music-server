package com.perrier.music.entity.artist;

import java.util.List;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class ArtistFindAllQuery extends FindQuery<List<Artist>> {
	
	@Override
	public List<Artist> query(Session session) throws DBException {
		@SuppressWarnings("unchecked")
		List<Artist> artists = session.createCriteria(Artist.class).list();
		return artists;
	}
}
