package com.perrier.music.entity.artist;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class ArtistFindByIdQuery extends FindQuery<Artist> {

	private final long id;
	
	public ArtistFindByIdQuery(long id) {
		this.id = id;
	}
	
	@Override
	public Artist query(Session session) throws DBException {
		Artist artist = (Artist) session.get(Artist.class, this.id);
		return artist;
	}
}
