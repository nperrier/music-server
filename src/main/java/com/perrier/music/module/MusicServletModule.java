package com.perrier.music.module;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;
import com.perrier.music.server.DefaultExceptionMapper;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class MusicServletModule extends ServletModule {

	public MusicServletModule() {
	}

	@Override
	protected void configureServlets() {

		// hook Jersey into Guice Servlet
		bind(GuiceContainer.class);

		// hook Jackson into Jersey as the POJO <-> JSON mapper
		bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
		bind(DefaultExceptionMapper.class).in(Scopes.SINGLETON);

		ImmutableMap<String, String> settings = ImmutableMap.of(//
				JSONConfiguration.FEATURE_POJO_MAPPING, "true", //
				PackagesResourceConfig.PROPERTY_PACKAGES, "com.perrier.music.rest.resource", //
				ResourceConfig.PROPERTY_RESOURCE_FILTER_FACTORIES, "com.perrier.music.server.auth.AuthorizationFilterFactory", //
				ServletContainer.PROPERTY_WEB_PAGE_CONTENT_REGEX, "/.*\\.(jpg|ico|png|gif|html|id|txt|css|js)");
		filter("/*").through(GuiceContainer.class, settings);
	}

}
