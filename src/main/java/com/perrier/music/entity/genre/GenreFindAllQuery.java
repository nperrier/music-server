package com.perrier.music.entity.genre;

import java.util.List;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class GenreFindAllQuery extends FindQuery<List<Genre>> {

	@Override
	public List<Genre> query(Session session) throws DBException {
		@SuppressWarnings("unchecked")
		List<Genre> genres = session.createCriteria(Genre.class).list();
		return genres;
	}

}
