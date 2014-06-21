package com.perrier.music.module;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;
import com.perrier.music.server.DefaultExceptionMapper;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class MusicServletModule extends ServletModule {

	@Override
	protected void configureServlets() {

		// hook Jersey into Guice Servlet
		this.bind(GuiceContainer.class);

		// hook Jackson into Jersey as the POJO <-> JSON mapper
		this.bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
		this.bind(DefaultExceptionMapper.class).in(Scopes.SINGLETON);
		
		ImmutableMap<String, String> settings = ImmutableMap.of(
				JSONConfiguration.FEATURE_POJO_MAPPING, "true",
				PackagesResourceConfig.PROPERTY_PACKAGES, "com.perrier.music.rest.resource",
				ServletContainer.PROPERTY_WEB_PAGE_CONTENT_REGEX, "/.*\\.(jpg|ico|png|gif|html|id|txt|css|js)");
		this.filter("/*").through(GuiceContainer.class, settings);
	}

}
