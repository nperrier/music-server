package com.perrier.music.entity.artist;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.DeleteQuery;

public class ArtistDeleteQuery extends DeleteQuery {

	private final Artist artist;

	public ArtistDeleteQuery(Artist artist) {
		this.artist = artist;
	}

	@Override
	public void query(Session session) throws DBException {
		session.delete(this.artist);
	}
}
