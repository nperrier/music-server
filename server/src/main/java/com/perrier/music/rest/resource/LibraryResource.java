package com.perrier.music.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.perrier.music.api.LibraryMetaData;
import com.perrier.music.db.DBException;

@Path("api/library")
@Produces(MediaType.APPLICATION_JSON)
public class LibraryResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public LibraryMetaData getAll() throws DBException {
		LibraryMetaData metaData = new LibraryMetaData();
		return metaData;
	}
}
