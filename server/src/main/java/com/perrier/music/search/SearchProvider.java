package com.perrier.music.search;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Splitter;
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
		String processedQuery = preprocessQuery(query);

		try {
			db.beginTransaction();
			SearchResults result = db.find(new FullTextSearchQuery(processedQuery));
			db.commit();
			return result;
		} finally {
			db.endTransaction();
		}
	}

	public SearchResults searchByTable(String query, SearchTable table) throws DBException {
		String processedQuery = preprocessQuery(query);

		try {
			db.beginTransaction();
			SearchResults result = db.find(new FullTextSearchByTableQuery(processedQuery, table));
			db.commit();
			return result;
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * Create a postgres tsquery data type from the plain user text input for use with the full text search functions
	 *
	 * @param rawQuery
	 * @return
	 */
	private String preprocessQuery(String rawQuery) {
		// normalize whitespace
		String processedQuery = StringUtils.normalizeSpace(rawQuery);

		// split search terms
		List<String> searchTerms = Splitter.on(" ").splitToList(processedQuery);
		processedQuery = searchTerms.stream()
				// add prefix searching to each term
				.map((term) -> term + ":*")
				// search using 'AND' junction on all terms
				.collect(Collectors.joining(" & "));

		return processedQuery;
	}
}
