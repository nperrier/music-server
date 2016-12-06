package com.perrier.music;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.perrier.music.api.LibraryMetaData;
import com.perrier.music.api.ServerAPI;
import com.perrier.music.indexer.Mp3DirectoryScanner;
import com.perrier.music.indexer.TrackUploaderService;
import com.perrier.music.storage.S3StorageService;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.jul.LevelChangePropagator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.util.StatusPrinter;

public class Core {

	private static final Logger log = LoggerFactory.getLogger(Core.class);

	private ServerAPI api;
	private S3StorageService storageService;
	private Mp3DirectoryScanner scanner;

	public static void main(String[] args) throws Exception {
		Thread t = Thread.currentThread();
		t.setName(Core.class.getSimpleName());

		Core core = new Core();

		Runtime.getRuntime().addShutdownHook(new Thread(Core.class.getSimpleName() + " SHUTDOWN") {

			@Override
			public void run() {
				core.stop();
			}
		});

		core.configureLogging();

		// Read command line props
		CommandLineArgs cmds = new CommandLineArgs();
		new JCommander(cmds, args);

		core.init(cmds);
		core.go(cmds);
		core.stop();
	}

	private void init(CommandLineArgs args) throws Exception {
		// setup server api
		try {
			int port = Integer.parseInt(args.port);
			this.api = new ServerAPI(args.host, port);
		} catch (NumberFormatException nfe) {
			throw new Exception("Port must be an integer", nfe);
		}
	}

	private void go(CommandLineArgs cmds) throws Exception {
		// handshake with server
		this.api.authenticate(cmds.username, cmds.password);
		// fetch library metadata
		LibraryMetaData libraryMetaData = this.api.getLibrary();

		initStorageService(cmds);

		TrackUploaderService uploaderService = new TrackUploaderService(this.storageService, this.api);

		scanner = new Mp3DirectoryScanner(cmds.path, libraryMetaData, uploaderService);
		scanner.start();

		log.info("Finished indexing");
	}

	private void initStorageService(CommandLineArgs cmds) {
		String accessKeyId = cmds.accessKeyId;
		String secretAccessKey = cmds.secretAccessKey;

		if (accessKeyId == null || secretAccessKey == null) {
			// read from home dir
			Properties props = new Properties();
			File credsFile = new File(System.getProperty("user.home") + "/.aws/credentials");
			try {
				InputStream input = new FileInputStream(credsFile);
				props.load(input);
				accessKeyId = props.getProperty("aws_access_key_id");
				secretAccessKey = props.getProperty("aws_secret_access_key");
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Unable to read amazon credentials file. Does not exist: " + credsFile);
			} catch (IOException e) {
				throw new RuntimeException("Unable to read amazon credentials file: " + credsFile);
			}
		}

		this.storageService = new S3StorageService(accessKeyId, secretAccessKey, cmds.awsBucket, cmds.awsRegion);
		try {
			this.storageService.startAsync().awaitRunning(30, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			throw new RuntimeException("Unable to start storage service: timed out after 30 seconds", e);
		}
	}

	private void configureLogging() {
		// Some third-party libraries (Jersey, JAudioTagger) use java.util.logging
		// Bridge to slf4
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		// Load logback config
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		// Clear any previous logback configuration:
		loggerContext.reset();

		LevelChangePropagator lcp = new LevelChangePropagator();
		lcp.setResetJUL(true);
		lcp.setContext(loggerContext);
		loggerContext.addListener(lcp);

		// configure logging (could move this to logback config file)
		PatternLayoutEncoder ple = new PatternLayoutEncoder();

		ple.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
		ple.setContext(loggerContext);
		ple.start();

		ConsoleAppender<ILoggingEvent> app = new ConsoleAppender<>();
		app.setEncoder(ple);
		app.setContext(loggerContext);
		app.start();

		configLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME, Level.ERROR, app);
		configLogger("com.perrier.music", Level.DEBUG, app);

		StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
	}

	private void configLogger(String name, Level level, Appender<ILoggingEvent> appender) {
		ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(name);
		logger.addAppender(appender);
		logger.setLevel(level);
		logger.setAdditive(false); /* set to true if root should log too */
	}

	private void stop() {
		if (this.scanner != null) {
			this.scanner.stop();
		}

		if (this.storageService != null && this.storageService.isRunning()) {
			try {
				this.storageService.stopAsync().awaitTerminated(5, TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				log.warn("Timeout while stopping Storage Service", e);
			}
		}
	}

	private static class CommandLineArgs {

		@Parameter(names = "-host", required = true, description = "Host of app")
		String host;

		@Parameter(names = "-port", required = true, description = "Port of app")
		String port;

		@Parameter(names = "-username", required = true, description = "Username for app")
		String username;

		@Parameter(names = "-password", description = "Password for app")
		String password;

		@Parameter(names = "-accessKeyId", description = "AWS access key ID")
		String accessKeyId;

		@Parameter(names = "-secretAccessKey", description = "AWS secret access key")
		String secretAccessKey;

		@Parameter(names = "-awsBucket", description = "AWS bucket")
		String awsBucket;

		@Parameter(names = "-awsRegion", description = "AWS region")
		String awsRegion;

		@Parameter(names = "-path", required = true, description = "Directory where music lives")
		String path;

	}
}