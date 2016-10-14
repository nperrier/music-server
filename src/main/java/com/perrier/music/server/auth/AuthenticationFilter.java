package com.perrier.music.server.auth;

import java.security.Principal;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;

import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.perrier.music.auth.LoginAuthenticator;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

	private final LoginAuthenticator loginAuthenticator;

	AuthenticationFilter(LoginAuthenticator loginAuthenticator) {
		this.loginAuthenticator = loginAuthenticator;
	}

	@Override
	public ContainerRequest filter(ContainerRequest request) {
		if (loginAuthenticator.isAuthDisabled()) {
			return request;
		}

		String token = extractToken(request);

		// create Principle with User (extract user from token)
		ReadOnlyJWTClaimsSet claims = loginAuthenticator.validateToken(token);
		//		String role = "admin"; /// TODO: roles
		//		User user = new User(claims.getSubjectClaim(), role);
		//		request.setSecurityContext(new MySecurityContext(null));
		return request;
	}

	private String extractToken(ContainerRequest request) {
		String token = null;
		// Extract the token from the HTTP Authorization header
		String authorizationHeader = request.getHeaderValue(HttpHeaders.AUTHORIZATION);
		if (!StringUtils.isBlank(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
			token = authorizationHeader.substring("Bearer".length()).trim();
		} else {
			// Check 'token' query param which should only be set for StreamingResource:
			MultivaluedMap<String, String> queryParameters = request.getQueryParameters();
			token = queryParameters.getFirst("token");
			return token;
		}

		return token;
	}

	private static class MySecurityContext implements SecurityContext {

		private final User user;

		private MySecurityContext(User user) {
			this.user = user;
		}

		@Override
		public Principal getUserPrincipal() {
			return () -> user.username;
		}

		@Override
		public boolean isUserInRole(String role) {
			return role.equals(user.role);
		}

		@Override
		public boolean isSecure() {
			return true;
		}

		@Override
		public String getAuthenticationScheme() {
			return "Basic";
		}
	}

	private static class User implements Principal {

		private final String username;
		private final String role;

		private User(String username, String role) {
			this.username = username;
			this.role = role;
		}

		@Override
		public String getName() {
			return username;
		}
	}
}