package com.perrier.music.entity.genre;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.DeleteQuery;

public class GenreDeleteQuery extends DeleteQuery {

	private final Genre genre;

	public GenreDeleteQuery(Genre genre) {
		this.genre = genre;
	}

	@Override
	public void query(Session session) throws DBException {
		session.delete(genre);
	}
}
