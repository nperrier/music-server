package com.perrier.music;

import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.perrier.music.config.ApplicationConfig;
import com.perrier.music.config.IConfiguration;
import com.perrier.music.db.IDatabase;
import com.perrier.music.indexer.ILibraryService;
import com.perrier.music.module.MusicModule;
import com.perrier.music.module.MusicServletModule;
import com.perrier.music.server.IServer;

public class Core {

	private Injector injector;
	private IDatabase db;
	private IServer server;
	private ILibraryService libraryService;
	private IConfiguration config;

	public static void main(String[] args) throws Exception {

		// Read command line props
		CommandLineArgs cmds = new CommandLineArgs();
		new JCommander(cmds, args);
		String configFile = cmds.configFile;
		if (configFile == null) {
			// Default config
			configFile = "conf/dev/config.groovy";
		}

		Core core = new Core();
		core.init(configFile);
	}

	private static class CommandLineArgs {

		@Parameter(names = "-config", description = "Config file for app")
		public String configFile;
	}

	private static void configureLogging() {
		// Some third-party libraries (Jersey, JAudioTagger) use java.util.logging
		// Bridge to slf4
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	public void init(String configFile) throws Exception {
		configureLogging();
		this.configAppProperties(configFile);
		this.createAppDirectories();
		this.createInjector();
		this.startServices();
	}

	@SuppressWarnings("unchecked")
	private void configAppProperties(String configFile) throws Exception {

		// config
		Map<String, Object> appConfig = new HashMap<String, Object>();

		// Set defaults first
		File appRoot = new File(System.getProperty("user.home") + File.separator + ".perrier");
		appConfig.put(ApplicationProperties.APP_ROOT.getKey(), appRoot.getAbsolutePath());

		File coverArtRoot = new File(appRoot.getAbsolutePath() + File.separator + "covers");
		appConfig.put(ApplicationProperties.COVERS_DIR.getKey(), coverArtRoot.getAbsolutePath());

		appConfig.putAll(loadConfigFromFile(configFile));

		this.config = new ApplicationConfig(appConfig);
	}

	private void createAppDirectories() throws Exception {

		File appRoot = new File(this.config.getRequiredString(ApplicationProperties.APP_ROOT));

		if (!appRoot.exists() && !appRoot.mkdir()) {
			throw new Exception("Unable to create application root: " + appRoot);
		}

		File coverRoot = new File(this.config.getRequiredString(ApplicationProperties.COVERS_DIR));

		if (!coverRoot.exists() && !coverRoot.mkdir()) {
			throw new Exception("Unable to create cover root: " + appRoot);
		}
	}

	public void createInjector() throws Exception {

		// modules
		final Module configModule = new AbstractModule() {

			@Override
			protected void configure() {
				this.binder().requireExplicitBindings();

				this.bind(IConfiguration.class).toInstance(Core.this.config);

				this.install(new MusicModule());
				this.install(new MusicServletModule());
			}
		};

		this.injector = Guice.createInjector(configModule);
	}

	public void startServices() throws Exception {

		// Need to ensure the order is correct here
		this.db = this.injector.getInstance(IDatabase.class);
		this.db.startAsync().awaitRunning();

		this.server = this.injector.getInstance(IServer.class);
		this.server.startAsync().awaitRunning();

		this.libraryService = this.injector.getInstance(ILibraryService.class);
		this.libraryService.startAsync().awaitRunning();

		// ServiceManager manager = new ServiceManager(services);
		// manager.startAsync().awaitHealthy();
	}

	@SuppressWarnings("rawtypes")
	private static Map loadConfigFromFile(String configPath) throws Exception {

		File configFile = new File(configPath);
		ConfigObject conf = new ConfigSlurper().parse(configFile.toURI().toURL());

		return conf.flatten();
	}

}
