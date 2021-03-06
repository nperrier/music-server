import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.INFO
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.jul.LevelChangePropagator
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender

// Reset Java logging
context = new LevelChangePropagator()
context.resetJUL = true

appender("CONSOLE", ConsoleAppender) {
	encoder(PatternLayoutEncoder) {
		pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
	}
}

// logpath is set by a system property
def logpath = System.getProperty("log_path")

appender("FILE", FileAppender) {
	file = "${logpath}/music-server.log"
	encoder(PatternLayoutEncoder) {
		pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
	}
}

logger("com.perrier.music", DEBUG)

// third party logging
logger("com.sun.jersey", ERROR)
logger("org.hibernate", ERROR)
logger("com.amazonaws", ERROR)
//logger("org.hibernate.SQL", INFO) // for showing sql
logger("org.eclipse.jetty", ERROR)
logger("org.jaudiotagger", ERROR)
logger("com.mchange.v2", ERROR)

root(INFO, ["CONSOLE", "FILE"])
