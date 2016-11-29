package com.perrier.music.entity.genre;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.UpdateQuery;

public class GenreUpdateQuery extends UpdateQuery<Genre> {

	private final Genre genre;

	public GenreUpdateQuery(Genre genre) {
		this.genre = genre;
	}

	@Override
	public Genre query(Session session) throws DBException {
		session.update(this.genre);
		return this.genre;
	}
}
