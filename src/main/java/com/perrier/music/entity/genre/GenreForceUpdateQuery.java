package com.perrier.music.entity.genre;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.ForceUpdateQuery;

public class GenreForceUpdateQuery extends ForceUpdateQuery<Object> {

	private final Genre genre;

	public GenreForceUpdateQuery(Genre genre) {
		this.genre = genre;
	}
	
	@Override
	public Object query(Session session) throws DBException {
        session.saveOrUpdate(this.genre);
        return this.genre;
	}
}
