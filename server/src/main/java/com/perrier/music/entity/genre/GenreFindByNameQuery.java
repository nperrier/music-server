package com.perrier.music.entity.genre;

import org.hibernate.Query;
import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class GenreFindByNameQuery extends FindQuery<Genre> {

	private final String name;

	public GenreFindByNameQuery(String name) {
		this.name = name;
	}

	@Override
	public Genre query(Session session) throws DBException {
		Query q = session.createQuery("from Genre where lower(name) = lower(:name)");
		q.setString("name", name);

		return (Genre) q.uniqueResult();
	}
}
