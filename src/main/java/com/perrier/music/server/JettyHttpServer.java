package com.perrier.music.server;

import java.util.EnumSet;
import javax.servlet.DispatcherType;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.perrier.music.config.IConfiguration;
import com.perrier.music.config.Property;

public class JettyHttpServer extends AbstractIdleService implements IServer {

	private static final Logger log = LoggerFactory.getLogger(JettyHttpServer.class);

	private final IConfiguration config;
	private Server server;

	public static final Property<Integer> PORT = new Property<Integer>("server.port");
	public static final Property<String> RESOURCE_BASE = new Property<String>("server.resource.base");

	@Inject
	public JettyHttpServer(IConfiguration config) throws ServerException {
		this.config = config;
		init();
	}

	public void init() throws ServerException {
		int port = this.config.getRequiredInteger(PORT);

		this.server = new Server(port);

		ServletContextHandler contextHandler = new ServletContextHandler();
		contextHandler.setResourceBase(config.getRequiredString(RESOURCE_BASE));
		contextHandler.setContextPath("/");

		// Filter for using Guice injection with Jersey resources
		contextHandler.addFilter(GuiceFilter.class, "/api/*",
				EnumSet.allOf(DispatcherType.class));

		// requires a single servlet
		contextHandler.addServlet(DefaultServlet.class, "/");

		server.setHandler(contextHandler);
		server.setStopAtShutdown(true);
	}

	@Override
	protected void startUp() throws Exception {
		log.info("Starting server");
		server.start();
		final int port = server.getConnectors()[0].getLocalPort();
		log.info("Server running on port {}", port);
	}

	@Override
	protected void shutDown() throws Exception {
		log.info("Stopping server");
		this.server.stop();
	}
}
