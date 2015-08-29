package com.perrier.music;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.collect.Maps;
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

import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;

public class Core {

	private static final Logger log = LoggerFactory.getLogger(Core.class);

	private Injector injector;
	private IDatabase db;
	private IServer server;
	private ILibraryService libraryService;
	private IConfiguration config;

	public static void main(String[] args) throws Exception {
		Thread t = Thread.currentThread();
		t.setName(Core.class.getSimpleName());

		// Read command line props
		CommandLineArgs cmds = new CommandLineArgs();
		new JCommander(cmds, args);
		String configFile = cmds.configFile;
		if (configFile == null) {
			// Default config
			configFile = "conf/config.groovy";
		}

		final Core core = new Core();

		Runtime.getRuntime().addShutdownHook(new Thread(Core.class.getSimpleName() + " SHUTDOWN") {

			@Override
			public void run() {
				core.stop();
			}
		});

		core.init(configFile);
	}

	private static class CommandLineArgs {

		@Parameter(names = "-config", description = "Config file for app")
		public String configFile;
	}

	private void configureLogging() {
		// Hibernate loggers that don't play nice. Turn them off via system props:
		System.setProperty("org.jboss.logging.provider", "slf4j");
		System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
		System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");

		// Some third-party libraries (Jersey, JAudioTagger) use java.util.logging
		// Bridge to slf4
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		String appDir = this.config.getRequiredString(ApplicationProperties.APP_ROOT);
		String logDir = this.config.getRequiredString(ApplicationProperties.LOG_DIR);
		String logPath = appDir + File.separator + logDir;
		System.setProperty("log_path", logPath);

		// Load logback config
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		ContextInitializer contextInitializer = new ContextInitializer(loggerContext);
		// Clear any previous logback configuration:
		loggerContext.reset();

		try {
			// Dynamically load a logback.groovy file:
			File logConfigFile = new File(this.config.getRequiredString(ApplicationProperties.LOG_CONFIGFILE));
			URL logConfigUrl = logConfigFile.toURI().toURL();
			contextInitializer.configureByResource(logConfigUrl);
		} catch (JoranException | MalformedURLException e) {
			e.printStackTrace();
		}

		StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
	}

	public void init(String configFile) throws Exception {
		this.configAppProperties(configFile);
		this.createAppDirectories();
		this.configureLogging();
		this.createDatabase();
		this.createInjector();
		this.startServices();
	}

	public void stop() {
		log.info("Shutting down...");
		this.stopServices();
		log.info("Application stopped");
	}

	@SuppressWarnings("unchecked")
	private void configAppProperties(String configFile) throws Exception {

		// config
		Map<String, Object> appConfig = Maps.newHashMap();

		// Set defaults first
		File appRoot = new File(System.getProperty("user.home") + File.separator + ".perrier");
		appConfig.put(ApplicationProperties.APP_ROOT.getKey(), appRoot.getAbsolutePath());

		File coverArtDir = new File(appRoot.getAbsolutePath() + File.separator + "covers");
		appConfig.put(ApplicationProperties.COVERS_DIR.getKey(), coverArtDir.getAbsolutePath());

		appConfig.putAll(loadConfigFromFile(configFile));

		this.config = new ApplicationConfig(appConfig);
	}

	private void createAppDirectories() throws Exception {

		File appRoot = new File(this.config.getRequiredString(ApplicationProperties.APP_ROOT));
		if (!appRoot.exists() && !appRoot.mkdir()) {
			throw new Exception("Unable to create application root: " + appRoot);
		}

		File coverDir = new File(appRoot, this.config.getRequiredString(ApplicationProperties.COVERS_DIR));
		if (!coverDir.exists() && !coverDir.mkdir()) {
			throw new Exception("Unable to create cover dir: " + coverDir);
		}

		File logDir = new File(appRoot, this.config.getRequiredString(ApplicationProperties.LOG_DIR));
		if (!logDir.exists() && !logDir.mkdir()) {
			throw new Exception("Unable to create log dir: " + logDir);
		}
	}

	public void createDatabase() throws Exception {
		Flyway flyway = new Flyway();

		String url = this.config.getRequiredString(ApplicationProperties.URL);
		String username = this.config.getRequiredString(ApplicationProperties.USERNAME);
		String password = this.config.getRequiredString(ApplicationProperties.PASSWORD);

		flyway.setDataSource(url, username, password);

		flyway.migrate();

		MigrationInfo info = flyway.info().current();

		log.info("DB version: " + info.getVersion());
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

	private void stopServices() {
		try {
			this.server.stopAsync().awaitTerminated(5, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			log.error("Timeout while stopping server");
		}
		try {
			this.libraryService.stopAsync().awaitTerminated(5, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			log.error("Timeout while stopping library service");
		}
		try {
			this.db.stopAsync().awaitTerminated(5, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			log.error("Timeout while stopping db");
		}
	}

	@SuppressWarnings("rawtypes")
	private static Map loadConfigFromFile(String configPath) throws Exception {

		File configFile = new File(configPath);
		ConfigObject conf = new ConfigSlurper().parse(configFile.toURI().toURL());

		return conf.flatten();
	}

}
