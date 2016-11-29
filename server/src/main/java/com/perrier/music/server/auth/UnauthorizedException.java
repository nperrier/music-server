package com.perrier.music.server.auth;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.perrier.music.api.ErrorInfo;

public class UnauthorizedException extends WebApplicationException {

	/**
	 * Create a HTTP 401 (Unauthorized) exception.
	 */
	public UnauthorizedException() {
		this(null, null);
	}

	/**
	 * Create a HTTP 401 (Unauthorized) exception.
	 *
	 * @param t
	 */
	public UnauthorizedException(Throwable t) {
		this(null, t);
	}

	/**
	 * Create a HTTP 401 (Unauthorized) exception.
	 *
	 * @param message the String that is the entity of the 401 response.
	 */
	public UnauthorizedException(String message) {
		this(message, null);
	}

	/**
	 * Create a HTTP 401 (Unauthorized) exception.
	 *
	 * @param t
	 * @param message the String that is the entity of the 401 response.
	 */
	public UnauthorizedException(String message, Throwable t) {
		super(t, Response.status(Status.UNAUTHORIZED) //
				.entity(new ErrorInfo(message, 401)) //
				.type(MediaType.APPLICATION_JSON) //
				.build());
	}
}
