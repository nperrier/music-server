package com.perrier.music.rest.resource;

import java.security.Principal;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.google.inject.Inject;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import com.perrier.music.db.DBException;
import com.perrier.music.entity.library.Library;
import com.perrier.music.entity.library.LibraryProvider;
import com.perrier.music.indexer.ILibraryIndexerService;
import com.perrier.music.indexer.ILibraryIndexerTaskFactory;
import com.perrier.music.server.EntityExistsException;
import com.perrier.music.server.EntityNotFoundException;

@Path("api/library")
@Produces(MediaType.APPLICATION_JSON)
public class LibraryResource {

	@Inject
	private LibraryProvider libraryProvider;

	@Inject
	private ILibraryIndexerService indexerService;

	@Inject
	private ILibraryIndexerTaskFactory taskFactory;

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") Long id) throws DBException {

		Library library = this.libraryProvider.findById(id);

		if (!DefaultGroovyMethods.asBoolean(library)) {
			throw new EntityNotFoundException("Library not found");
		}

		return Response.ok(library).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() throws DBException {
		List<Library> libraries = this.libraryProvider.findAll();
		return Response.ok(libraries).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed("Admin")
	public Response createLibrary(Library library, @Context SecurityContext securityContext) throws DBException {

		if (securityContext != null) {
			Principal principal = securityContext.getUserPrincipal();
			boolean role = securityContext.isUserInRole("Admin");
		}

		// TODO: Wrap all this logic in a separate class that throws exceptions that the API translates to Response codes
		// TODO validate the path before creating
		Library lib = this.libraryProvider.findByPath(library.getPath());

		if (DefaultGroovyMethods.asBoolean(lib)) {
			throw new EntityExistsException("Library already exists");
		}

		this.libraryProvider.create(library);

		// start a scan immediately
		this.indexerService.submit(this.taskFactory.create(library));

		return Response.status(Response.Status.CREATED).entity(library).build();
	}

	@POST
	@Path("{id}/scan")
	@Produces(MediaType.APPLICATION_JSON)
	public Response scan(@PathParam("id") Long id) throws DBException {

		Library library = this.libraryProvider.findById(id);
		if (!DefaultGroovyMethods.asBoolean(library)) {
			throw new EntityExistsException("Library does not exist");
		}

		this.indexerService.submit(this.taskFactory.create(library));

		return Response.status(Response.Status.CREATED).build();
	}

}