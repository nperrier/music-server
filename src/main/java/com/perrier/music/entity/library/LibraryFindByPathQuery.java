package com.perrier.music.entity.library;

import org.hibernate.Query;
import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class LibraryFindByPathQuery extends FindQuery<Library> {

	private final String path;
	
	public LibraryFindByPathQuery(String path) {
		this.path = path;
	}
	
	@Override
	public Library query(Session session) throws DBException {
		
		Query query = session.createQuery("from Library where path = :path");
		query.setString("path", path);
		Library library = (Library) query.uniqueResult();
		
		return library;
	}

}
