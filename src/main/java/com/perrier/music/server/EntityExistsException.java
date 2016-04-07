package com.perrier.music.server;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.Responses;

public class EntityExistsException extends WebApplicationException {

	private static final long serialVersionUID = 6447141531941580586L;

	/**
	 * Create a HTTP 409 (Conflict) exception.
	 *
	 * @param message the String that is the entity of the 409 response.
	 */
	public EntityExistsException(String message) {
		super(Response.status(Responses.CONFLICT) //
				.entity(new ErrorInfo(message, Responses.CONFLICT)) //
				.type(MediaType.APPLICATION_JSON) //
				.build());
	}

}