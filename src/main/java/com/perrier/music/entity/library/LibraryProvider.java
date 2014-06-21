package com.perrier.music.entity.library;

import java.util.List;

import com.google.inject.Inject;
import com.perrier.music.db.DBException;
import com.perrier.music.db.IDatabase;

public class LibraryProvider {

	private IDatabase db;

	@Inject
	public LibraryProvider(IDatabase db) {
		this.db = db;
	}

	public Library findById(long id) throws DBException {
		Library library = (Library) this.db.find(new LibraryFindByIdQuery(id));
		return library;
	}
	
	public Library findByPath(String path) throws DBException {
		Library library = this.db.find(new LibraryFindByPathQuery(path));
		return library;
	}
	
	public void create(Library library) throws DBException {
		this.db.create(new LibraryCreateQuery(library));
	}

	public void update(Library library) throws DBException {
		this.db.update(new LibraryUpdateQuery(library));
	}
	public List<Library> findAll() throws DBException {
		List<Library> libraries = this.db.find(new LibraryFindAllQuery());
		return libraries;
	}
}
