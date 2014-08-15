
import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.INFO
import static ch.qos.logback.classic.Level.OFF
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.jul.LevelChangePropagator
import ch.qos.logback.core.ConsoleAppender

// Reset Java logging
context = new LevelChangePropagator()
context.resetJUL = true

appender("STDOUT", ConsoleAppender) {
	encoder(PatternLayoutEncoder) { pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n" }
}

logger("com.perrier.music", DEBUG)

// third party logging
logger("com.sun.jersey", ERROR)
logger("org.hibernate", ERROR)
logger("org.hibernate.SQL", INFO) // for showing sql
logger("org.eclipse.jetty", ERROR)
logger("org.jaudiotagger", ERROR)
logger("com.mchange.v2", ERROR)

root(INFO, ["STDOUT"])
