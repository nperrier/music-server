package com.perrier.music.entity.artist;

import org.hibernate.Query;
import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class ArtistFindByNameQuery extends FindQuery<Artist> {

	private final String name;

	public ArtistFindByNameQuery(String name) {
		this.name = name;
	}

	@Override
	public Artist query(Session session) throws DBException {
		Query q = session.createQuery("from Artist where lower(name) = lower(:name)");
		q.setString("name", name);

		return (Artist) q.uniqueResult();
	}

}
