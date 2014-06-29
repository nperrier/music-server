package com.perrier.music.entity.genre;

import java.util.List;

import com.google.inject.Inject;
import com.perrier.music.db.DBException;
import com.perrier.music.db.IDatabase;

public class GenreProvider {

	private final IDatabase db;

	@Inject
	public GenreProvider(IDatabase db) {
		this.db = db;
	}

	public List<Genre> findAll() throws DBException {
		List<Genre> genres = this.db.find(new GenreFindAllQuery());
		return genres;
	}
}
