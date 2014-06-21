package com.perrier.music.entity.library;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.UpdateQuery;

public class LibraryUpdateQuery extends UpdateQuery<Library> {

	private final Library library;

	public LibraryUpdateQuery(Library library) {
		this.library = library;
	}

	@Override
	public Library query(Session session) throws DBException {

		session.update(library);
		return this.library;
	}
}
