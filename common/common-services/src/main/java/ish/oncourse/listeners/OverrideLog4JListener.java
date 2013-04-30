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
	protected static final String APPLICATION_NAME_CONTEXT_PARAM = "applicationName";
	static final String TRUE_STRING = "true";
	protected static final String LOG4J_DEFAULT_OVERRIDE_SYSTEM_PROPERTY = "log4j.defaultInitOverride";
	protected static final String PROPERTIES_EXTENSION = ".properties";
	protected static final String LOG4J_PREFIX_FILE_NAME = "log4j";
	protected static final String CONF_FOLDER_NAME = "/conf/";
	protected static final String CATALINA_HOME_SYSTEM_PROPERTY = "catalina.home";
	protected static final String DEFAULT_LOG4J_FILENAME = LOG4J_PREFIX_FILE_NAME + PROPERTIES_EXTENSION;
	protected static final String DEFAULT_LOG4J_PROPERTIES_FILE_LOCATION = "/WEB-INF/classes/" + DEFAULT_LOG4J_FILENAME;
	protected static final String APPLICATION_CUSTOM_LOGGER_FILE_FORMAT = LOG4J_PREFIX_FILE_NAME + ".%s" + PROPERTIES_EXTENSION;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		initTheLogger(sce.getServletContext());
	}
		
	static void initTheLogger(ServletContext context) {
		LogManager.resetConfiguration();
		System.setProperty(LOG4J_DEFAULT_OVERRIDE_SYSTEM_PROPERTY, TRUE_STRING);
		//firstly try to load the location defined in TomCat
		String applicationName = context.getInitParameter(APPLICATION_NAME_CONTEXT_PARAM);
		boolean isLoggingInit = false;
		//load the application custom logger attempt
		if (StringUtils.trimToNull(applicationName) != null) {
			isLoggingInit = initConfiguration(context, String.format(APPLICATION_CUSTOM_LOGGER_FILE_FORMAT, applicationName));
		}
		//load the common custom logger attempt
		if (!isLoggingInit) {
			isLoggingInit = initConfiguration(context, DEFAULT_LOG4J_FILENAME);
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
	
	protected static boolean initConfiguration(ServletContext context, String fileName) {
		if (StringUtils.trimToNull(fileName) != null) {
			String catalinaHome = System.getProperty(CATALINA_HOME_SYSTEM_PROPERTY);
			String confFilePath = catalinaHome + CONF_FOLDER_NAME + fileName;
			return initialiseLoggingSystemWithFile(context, confFilePath);
		}
		return false;
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
