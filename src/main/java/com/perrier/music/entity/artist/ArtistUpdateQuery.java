package com.perrier.music.entity.artist;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.UpdateQuery;

public class ArtistUpdateQuery extends UpdateQuery<Artist> {

	private final Artist artist;

	public ArtistUpdateQuery(Artist artist) {
		this.artist = artist;
	}

	@Override
	public Artist query(Session session) throws DBException {
		session.update(artist);
		return artist;
	}
}
