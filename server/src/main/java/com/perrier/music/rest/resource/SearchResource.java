package com.perrier.music.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.perrier.music.api.SearchResultsDto;
import com.perrier.music.db.DBException;
import com.perrier.music.rest.SearchResultsDtoMapper;
import com.perrier.music.search.SearchProvider;
import com.perrier.music.search.SearchResults;
import com.perrier.music.search.SearchTable;
import com.perrier.music.server.EntityNotFoundException;

@Path("api/search")
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {

	@Inject
	private SearchProvider searchProvider;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultsDto getAll(@QueryParam("q") String query) throws DBException {
		SearchResults searchResults = searchProvider.searchAll(query);
		SearchResultsDto dto = SearchResultsDtoMapper.build(searchResults);

		return dto;
	}

	@GET
	@Path("{table}")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultsDto get(@QueryParam("q") String query, @PathParam("table") String table) throws DBException {
		SearchTable searchTable;
		try {
			searchTable = SearchTable.valueOf(table.toUpperCase());
		} catch (IllegalArgumentException iae) {
			throw new EntityNotFoundException();
		}
		SearchResults searchResults = searchProvider.searchByTable(query, searchTable);
		SearchResultsDto dto = SearchResultsDtoMapper.build(searchResults);

		return dto;
	}

}
