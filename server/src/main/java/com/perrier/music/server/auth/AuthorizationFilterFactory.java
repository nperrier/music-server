package com.perrier.music.server.auth;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;
import com.perrier.music.auth.LoginAuthenticator;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;

public class AuthorizationFilterFactory implements ResourceFilterFactory {

	private final LoginAuthenticator loginAuthenticator;
	private final ResourceFilter authFilter;

	@Inject
	public AuthorizationFilterFactory(LoginAuthenticator loginAuthenticator) {
		this.loginAuthenticator = loginAuthenticator;
		// single instance
		this.authFilter = new ResourceFilter() {

			@Override
			public ContainerRequestFilter getRequestFilter() {
				return new AuthenticationFilter(AuthorizationFilterFactory.this.loginAuthenticator);
			}

			@Override
			public ContainerResponseFilter getResponseFilter() {
				return null;
			}
		};
	}

	@Override
	public List<ResourceFilter> create(AbstractMethod abstractMethod) {
		if (abstractMethod.isAnnotationPresent(NoAuthentication.class)) {
			return null;
		} else {
			return Arrays.asList(this.createAuthorizationFilter());
		}
	}

	private ResourceFilter createAuthorizationFilter() {
		return authFilter;
	}
}