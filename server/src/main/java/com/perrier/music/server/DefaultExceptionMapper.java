package com.perrier.music.server;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.perrier.music.api.ErrorInfo;
import com.sun.jersey.api.NotFoundException;

@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Logger log = LoggerFactory.getLogger(DefaultExceptionMapper.class);

	private static final ErrorInfo ERROR_INFO = new ErrorInfo("An internal server error occurred",
			HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

	@Override
	public Response toResponse(Throwable t) {

		Response r = null;

		// Let Jersey do its thing
		if (t instanceof WebApplicationException) {
			WebApplicationException e = ((WebApplicationException) t);
			String errorMessage = "Error: " + e.getMessage();
			
			if (e instanceof NotFoundException) {
				NotFoundException nfe = (NotFoundException) e;
				errorMessage = "Resource not found: " + nfe.getNotFoundUri().getPath();
			}
			
			r = e.getResponse();
			if (r.getEntity() == null) {
				r = Response.fromResponse(r) //
						.entity(new ErrorInfo(errorMessage,
								r.getStatus())) //
						.type(MediaType.APPLICATION_JSON) //
						.build();
			}

			return r;
		}

		// Application exceptions
		log.error("Uncaught exception", t);
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR) //
				.entity(ERROR_INFO) //
				.type(MediaType.APPLICATION_JSON) //
				.build();
	}
}