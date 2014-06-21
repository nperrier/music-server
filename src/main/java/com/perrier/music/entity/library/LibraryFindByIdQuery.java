package com.perrier.music.entity.library;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class LibraryFindByIdQuery extends FindQuery<Library> {

	private final long id;

	public LibraryFindByIdQuery(long id) {
		this.id = id;
	}
	
	@Override
	public Library query(Session session) throws DBException {
		Library library = (Library) session.get(Library.class, id);
		return library;
	}
}
