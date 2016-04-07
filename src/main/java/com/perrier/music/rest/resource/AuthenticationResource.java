package com.perrier.music.rest.resource;

import java.io.Serializable;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hibernate.validator.constraints.NotBlank;

import com.google.inject.Inject;
import com.perrier.music.auth.LoginAuthenticator;
import com.perrier.music.server.auth.NoAuthentication;

@Path("/api/authentication")
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationResource {

	@Inject
	private LoginAuthenticator loginAuthenticator;

	@Inject
	private Validator validator;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@NoAuthentication
	public Response createToken(AuthRequest creds) {

		Set<ConstraintViolation<AuthRequest>> violations = validator.validate(creds);

		if (violations.isEmpty()) {
			// Authenticate
			String token = loginAuthenticator.authenticate(creds.getUsername(), creds.getPassword());
			// Return the token on the response
			return Response.ok(new AuthResponse(token)).build();
		} else {
			// TODO: enumerate violations in response
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
	}

	private static class AuthRequest implements Serializable {

		@NotBlank
		private String username;
		@NotBlank
		private String password;

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}
	}

	private static class AuthResponse {

		private String token;

		AuthResponse(String token) {
			this.token = token;
		}

		public String getToken() {
			return this.token;
		}
	}
}
