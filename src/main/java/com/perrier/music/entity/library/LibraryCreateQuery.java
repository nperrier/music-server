package com.perrier.music.entity.library;

import org.hibernate.Session;

import com.perrier.music.db.CreateQuery;
import com.perrier.music.db.DBException;

public class LibraryCreateQuery extends CreateQuery<Object> {

	private final Library library;
	
	public LibraryCreateQuery(Library library) {
		this.library = library;
	}
	
	@Override
	public Object query(Session session) throws DBException {
		session.save(this.library);
		return this.library;
	}
}
