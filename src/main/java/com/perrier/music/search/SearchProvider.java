package com.perrier.music.search;

import com.google.inject.Inject;
import com.perrier.music.db.DBException;
import com.perrier.music.db.IDatabase;

public class SearchProvider {

	private final IDatabase db;

	@Inject
	public SearchProvider(IDatabase db) {
		this.db = db;
	}

	public SearchResults searchAll(String query) throws DBException {
		try {
			db.beginTransaction();
			SearchResults result = db.find(new FullTextSearchQuery(query));
			db.commit();
			return result;
		} finally {
			db.endTransaction();
		}
	}

	public SearchResults searchByTable(String query, SearchTable table) throws DBException {
		try {
			db.beginTransaction();
			SearchResults result = db.find(new FullTextSearchByTableQuery(query, table));
			db.commit();
			return result;
		} finally {
			db.endTransaction();
		}
	}
}
