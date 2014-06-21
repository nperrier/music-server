package com.perrier.music.entity.library;

import java.util.List;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class LibraryFindAllQuery extends FindQuery<List<Library>> {

	@Override
	public List<Library> query(Session session) throws DBException {
		@SuppressWarnings("unchecked")
		List<Library> libraries = session.createCriteria(Library.class).list();
		return libraries;
	}
}
