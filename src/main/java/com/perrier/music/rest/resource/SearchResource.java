package com.perrier.music.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.perrier.music.db.DBException;

@Path("api/search")
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@QueryParam("q") String query) throws DBException {

		return Response.ok().build();
	}
}
