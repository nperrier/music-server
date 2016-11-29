package com.perrier.music.entity.artist;

import org.hibernate.Session;

import com.perrier.music.db.CreateQuery;
import com.perrier.music.db.DBException;

public class ArtistCreateQuery extends CreateQuery<Artist> {

	private final Artist artist;
	
	public ArtistCreateQuery(Artist artist) {
		this.artist = artist;
	}
	
	@Override
	public Artist query(Session session) throws DBException {
		session.save(artist);
		return artist;
	}

}
