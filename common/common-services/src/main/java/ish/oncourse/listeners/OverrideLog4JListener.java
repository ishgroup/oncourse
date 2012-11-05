package ish.oncourse.listeners;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

public class OverrideLog4JListener implements ServletContextListener {

	protected static final String TOMCAT_CONFIGURED_LOG4J_PROPERTIES_FILE_LOCATION = "/conf/log4j.properties";
	protected static final String DEFAULT_LOG4J_PROPERTIES_FILE_LOCATION = "/WEB-INF/classes/log4j.properties";

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		initTheLogger(sce.getServletContext());
	}
	
	static void initTheLogger(ServletContext context) {
		LogManager.resetConfiguration();
		System.setProperty("log4j.defaultInitOverride", "true");
		//firstly try to load the location defined in TomCat
		String confFilePath = null;
		String catalinaHome = System.getProperty("catalina.home");
		if (StringUtils.trimToNull(catalinaHome) != null) {
			confFilePath = catalinaHome + TOMCAT_CONFIGURED_LOG4J_PROPERTIES_FILE_LOCATION;
		}
		boolean isLoggingInit = false;
		if (StringUtils.trimToNull(confFilePath) != null) {
			isLoggingInit = initialiseLoggingSystemWithFile(context, confFilePath);
		}
		//if conf loging not inited, init the default logging settings
		if (!isLoggingInit) {
			URL confURL = null;
			try {
				confURL = context.getResource(DEFAULT_LOG4J_PROPERTIES_FILE_LOCATION);
			} catch (MalformedURLException e) {
				logException(context, "Failed to load war logger properties", e);
				confURL = null;
			}
			if (confURL != null) {
				isLoggingInit = initialiseSystemLoggingFromURL(context, confURL);
			}
		}
		if (!isLoggingInit) {
			outputMessage(context, "Unable to init the Logger! Conf log4j.properties and application log4j.properties not able to be loaded!");
		}
	}
	
	protected static void outputMessage(ServletContext context, String message) {
		logException(context, message, null);
	}
	
	protected static void logException(ServletContext context, String message, Throwable throwable) {
		if (throwable != null) {
			context.log(message, throwable);
		} else {
			context.log(message);
		}
	}
	
	/**
	 * When initialising the system logging with file we can watch the file and refresh the configuration
	 * @param pathToLogFile
	 */
	protected static boolean initialiseSystemLoggingFromURL(ServletContext context, URL logFileURL) {
		if (logFileURL != null) {
			PropertyConfigurator.configure(logFileURL);
			outputMessage(context, "...successfully initialised log properties from file: '" + logFileURL.getFile() + "'");
			return true;
		} else {
			outputMessage(context, "...failed to initialise log properties from undefined file : ");
			return false;
		}
	}
	
	protected static boolean initialiseLoggingSystemWithFile(ServletContext context, String pathToLogFile) {
		try {
			final File customLogFile = new File(pathToLogFile);
			if (customLogFile.exists() && !customLogFile.isDirectory() && customLogFile.canRead()) {
				PropertyConfigurator.configureAndWatch(customLogFile.getCanonicalPath(), 60 * 1000L);
				outputMessage(context, "...successfully initialised log properties from file: '" + pathToLogFile + "'");
				return true;
			}
		} catch (final IOException e) {
			outputMessage(context, "...failed to initialise log properties from file : '" + pathToLogFile + "'");
		}
		return false;
	}

}
