package com.perrier.music;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.perrier.music.config.ApplicationConfig;
import com.perrier.music.config.IConfiguration;
import com.perrier.music.db.IDatabase;
import com.perrier.music.module.MusicModule;
import com.perrier.music.module.MusicServletModule;
import com.perrier.music.server.JettyHttpServer;
import com.perrier.music.storage.S3StorageService;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;

public class Core {

	private static final Logger log = LoggerFactory.getLogger(Core.class);

	private volatile boolean initialized;

	private Injector injector;
	private IDatabase db;
	private JettyHttpServer server;
	private IConfiguration config;
	private S3StorageService storageClient;

	public static void main(String[] args) throws Exception {
		try {
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
			Integer port = cmds.port;

			final Core core = new Core();

			Runtime.getRuntime().addShutdownHook(new Thread(Core.class.getSimpleName() + " SHUTDOWN") {

				@Override
				public void run() {
					core.stop();
				}
			});

			core.init(configFile, port);
		} catch (Throwable t) {
			log.error("Error while starting application", t);
		}
	}

	private static class CommandLineArgs {

		@Parameter(names = "-config", description = "Config file for app")
		private String configFile;

		@Parameter(names = "-port", description = "Port the app will listen for requests on")
		private Integer port;
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

		String logDir = this.config.getRequiredString(ApplicationProperties.LOG_DIR);
		System.setProperty("log_path", logDir);

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

	@VisibleForTesting
	void init(String configFile, Integer port) throws Exception {
		this.configAppProperties(configFile, port);
		this.addMimeTypes();
		this.createAppDirectories();
		this.configureLogging();
		this.createDatabase();
		this.createInjector();
		this.startServices();

		this.initialized = true;
	}

	/**
	 * Add additional mime types to java's default MimetypesFileTypeMap
	 */
	private void addMimeTypes() {
		List mimeTypes = this.config.getOptionalList(ApplicationProperties.MIME_TYPES);
		MimetypesFileTypeMap defaultFileTypeMap = (MimetypesFileTypeMap) MimetypesFileTypeMap.getDefaultFileTypeMap();
		mimeTypes.forEach((mimeType) -> {
			defaultFileTypeMap.addMimeTypes((String) mimeType);
		});
	}

	private void stop() {
		log.info("Shutting down...");
		this.stopServices();
		log.info("Application stopped");
	}

	@SuppressWarnings("unchecked")
	private void configAppProperties(String configFile, Integer port) throws Exception {
		// config
		Map<String, Object> appConfig = Maps.newHashMap();

		// Set defaults first
		File appRoot = new File(System.getProperty("user.home") + File.separator + ".perrier");
		appConfig.put(ApplicationProperties.APP_ROOT.getKey(), appRoot.getAbsolutePath());

		File coverArtDir = new File(appRoot.getAbsolutePath() + File.separator + "covers");
		appConfig.put(ApplicationProperties.COVERS_DIR.getKey(), coverArtDir.getAbsolutePath());

		// set any properties from environment next:
		appConfig.putAll(System.getenv());

		// If db url is specified via env vars, we need to derive the user/pass combo and set the appropriate properties
		String dbUrl = System.getenv("DATABASE_URL");
		String jdbcUrl = System.getenv("JDBC_DATABASE_URL");
		if (!StringUtils.isBlank(dbUrl) && !StringUtils.isBlank(jdbcUrl)) {
			URI dbUri = new URI(dbUrl);
			String userInfo = dbUri.getUserInfo();
			appConfig.put(ApplicationProperties.USERNAME.getKey(), userInfo.split(":")[0]);
			appConfig.put(ApplicationProperties.PASSWORD.getKey(), userInfo.split(":")[1]);
			appConfig.put(ApplicationProperties.URL.getKey(), jdbcUrl);
		}

		// set from config file:
		appConfig.putAll(loadConfigFromFile(configFile));

		// override port from command line, if present:
		if (port != null && port > 1024) {
			appConfig.put(ApplicationProperties.PORT.getKey(), port);
		}

		this.config = new ApplicationConfig(appConfig);
	}

	private void createAppDirectories() throws Exception {

		File appRoot = new File(this.config.getRequiredString(ApplicationProperties.APP_ROOT));
		if (!appRoot.exists() && !appRoot.mkdir()) {
			throw new Exception("Unable to create application root: " + appRoot);
		}

		File coverDir = new File(this.config.getRequiredString(ApplicationProperties.COVERS_DIR));
		if (!coverDir.exists() && !coverDir.mkdir()) {
			throw new Exception("Unable to create cover dir: " + coverDir);
		}

		File logDir = new File(this.config.getRequiredString(ApplicationProperties.LOG_DIR));
		if (!logDir.exists() && !logDir.mkdir()) {
			throw new Exception("Unable to create log dir: " + logDir);
		}
	}

	private void createDatabase() throws Exception {
		Flyway flyway = new Flyway();

		String url = this.config.getRequiredString(ApplicationProperties.URL);
		String username = this.config.getRequiredString(ApplicationProperties.USERNAME);
		String password = this.config.getRequiredString(ApplicationProperties.PASSWORD);

		flyway.setDataSource(url, username, password);

		flyway.migrate();

		MigrationInfo info = flyway.info().current();

		log.info("DB version: {}", info.getVersion());
	}

	private void createInjector() throws Exception {
		log.debug("Creating Injector");

		// modules
		final Module configModule = new AbstractModule() {

			@Override
			protected void configure() {
				this.binder().requireExplicitBindings();

				this.bind(IConfiguration.class).toInstance(Core.this.config);

				this.install(new MusicModule(Core.this.config));
				this.install(new MusicServletModule());
			}
		};

		this.injector = Guice.createInjector(configModule);
	}

	private void startServices() throws Exception {

		// Need to ensure the order is correct here
		this.db = this.injector.getInstance(IDatabase.class);
		this.db.startAsync().awaitRunning();

		this.storageClient = this.injector.getInstance(S3StorageService.class);
		this.storageClient.startAsync().awaitRunning();

		this.server = this.injector.getInstance(JettyHttpServer.class);
		this.server.startAsync().awaitRunning();

		// ServiceManager manager = new ServiceManager(services);
		// manager.startAsync().awaitHealthy();
	}

	private void stopServices() {
		stopService(this.storageClient);
		stopService(this.server);
		stopService(this.db);
	}

	private void stopService(Service service) {
		if (service == null) {
			return;
		}
		try {
			log.info("Stopping service {}", service.getClass().getSimpleName());
			service.stopAsync().awaitTerminated(5, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			log.error("Timeout while stopping service {}", service.getClass().getSimpleName());
		}
	}

	@SuppressWarnings("rawtypes")
	private static Map loadConfigFromFile(String configPath) throws Exception {

		File configFile = new File(configPath);
		ConfigObject conf = new ConfigSlurper().parse(configFile.toURI().toURL());

		return conf.flatten();
	}

	public Injector getInjector() {
		if (!this.initialized) {
			throw new RuntimeException("Not initialized!");
		}

		return this.injector;
	}

}
