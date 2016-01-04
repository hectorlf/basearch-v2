 package basearch.integration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;

public class ApplicationInitializerListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationInitializerListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.debug("Shutting down application...");
		// logback's async appender shutdown
		((LoggerContext)LoggerFactory.getILoggerFactory()).stop();
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.debug("Starting up application...");
	}

}
