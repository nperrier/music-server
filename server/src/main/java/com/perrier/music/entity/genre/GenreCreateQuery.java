package com.perrier.music.entity.genre;

import org.hibernate.Session;

import com.perrier.music.db.CreateQuery;
import com.perrier.music.db.DBException;

public class GenreCreateQuery extends CreateQuery<Genre> {

	private final Genre genre;

	public GenreCreateQuery(Genre genre) {
		this.genre = genre;
	}

	@Override
	public Genre query(Session session) throws DBException {
		session.save(this.genre);
		return this.genre;
	}
}
