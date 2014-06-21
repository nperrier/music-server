package com.perrier.music.server;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.Responses;

public class EntityNotFoundException extends WebApplicationException {

	private static final long serialVersionUID = 6447141531941580586L;

	/**
	 * Create a HTTP 404 (Not Found) exception.
	 */
	public EntityNotFoundException() {
		this("Entity Not Found");
	}

	/**
	 * Create a HTTP 404 (Not Found) exception.
	 * 
	 * @param message the String that is the entity of the 404 response.
	 */
	public EntityNotFoundException(String message) {
		super(Response.status(Responses.NOT_FOUND) //
				.entity(new ErrorInfo(message, Responses.NOT_FOUND)) //
				.type(MediaType.APPLICATION_JSON) //
				.build());
	}

}