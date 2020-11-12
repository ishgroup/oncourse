/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.common;

import ish.util.SecurityUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.status.StatusLogger;

import javax.activation.FileTypeMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * class providing utility methods for everything related to resources: icons, files, streams etc.
 */
public final class ResourcesUtil {

	private static final Logger logger = LogManager.getLogger();

	private static final String LOG_PROPERTIES_DIR = "resources/";
	private static final String LOG_CONFIG = "logSetup.xml";

	/**
	 * default constructor hidden from users
	 */
	private ResourcesUtil() {}

	/**
	 * Get mime type by file name. List of supported types can be found at <code>META-INF/mime.types</code>
	 *
	 * @param fileUrl
	 * @return string mime type
	 */
	public static String getMimeType(String fileUrl) {
		return FileTypeMap.getDefaultFileTypeMap().getContentType(fileUrl);
	}

	/**
	 * returns properly formatted URL for a given class.
	 *
	 * @param clazz to find jar for
	 * @return URL of the jar
	 */
	public static URL getRunningJarUrl(Class<?> clazz) {
		String className = clazz.getName().replace('.', '/');
		URL classJar = clazz.getResource("/" + className + ".class");
		if (!classJar.toString().startsWith("jar:")) {
			return null;
		}

        String[] vals = classJar.toString().split("!");
		try {
			return new URL(vals[0] + "!/");
		} catch (MalformedURLException e) {
			logger.catching(e);
			return null;
		}

	}

	/**
	 * @return the URL of the jar of the package where ResourcesUtil is implemented
	 */
	public static URL getRunningJarUrl() {
		return getRunningJarUrl(ResourcesUtil.class);
	}

	public static String getReleaseVersionString() {
		try {
			JarURLConnection conn = (JarURLConnection) getRunningJarUrl().openConnection();
			return conn.getManifest().getMainAttributes().getValue("Release-Version");
		} catch (Exception e) {
			return "development"; // when in development mode
		}
	}

	/**
	 * @param relativePath
	 * @return input stream
	 */
	public static InputStream getResourceAsInputStream(final String relativePath) {
		logger.entry(relativePath);

		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(relativePath);
		if (is != null) {
			return is;
		}

		logger.debug("resource not found, searched in paths:\n {}", getClasspathString());
		return null;
	}

	public static String getClasspathString() {
		StringBuilder classpath = new StringBuilder();
		String pathSeparator = System.getProperty("path.separator");
		String[] classPathEntries = System.getProperty("java.class.path") .split(pathSeparator);
		try {
			for(String classPathEntry : classPathEntries) {
				classpath.append(new File(classPathEntry).toURI().toURL().toString()).append("\r\n");
//				classpath.append(new URL(classPathEntry).getFile()).append("\r\n");
			}
		} catch (MalformedURLException e) {
			logger.warn("Resources weren't found.");
		}

		return classpath.toString();
	}

	/**
	 * initialises log4j
	 */
	public static void initialiseLogging(boolean createConfigurationFile) {
		Logger logLogger = StatusLogger.getLogger();
		logLogger.traceEntry();

		if (createConfigurationFile) {
			File logConfigFile = new File(LOG_CONFIG);
			if (! logConfigFile.exists()) {
				logLogger.debug("Log configuration file missing at {}", LOG_CONFIG);
				try {
					if (getResourceAsInputStream(LOG_PROPERTIES_DIR + LOG_CONFIG) == null) {
						throw new NullPointerException("Log config was not found");
					}
					FileUtils.copyInputStreamToFile(getResourceAsInputStream(LOG_PROPERTIES_DIR + LOG_CONFIG), logConfigFile);
				} catch (IOException e) {
					logLogger.catching(Level.ERROR, e);
				}
			}
			System.setProperty("log4j.configurationFile", LOG_CONFIG);
		} else {
			System.setProperty("log4j.configurationFile", LOG_PROPERTIES_DIR + LOG_CONFIG);
		}

		logLogger.traceExit();

		//set log configurations and reconfigure it because this property has already initialized
		LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
		logContext.reconfigure();
	}

	/**
	 * @param file
	 * @return byte[] representation of a file
	 * @throws IOException
	 */
	public static byte[] fileToByteArray(final File file) throws IOException {
		return FileUtils.readFileToByteArray(file);
	}

	/**
	 * read file, return contents of the file as String
	 *
	 * @param relativePath to the file
	 * @return contexts of the file
	 */
	public static String readFile(String relativePath) {
		logger.entry(relativePath);
		InputStream is = null;

		try {
			final File aResource = new File(relativePath);

			if (aResource.exists()) {
				return FileUtils.readFileToString(aResource);
			}

			is = getResourceAsInputStream(relativePath);

			return IOUtils.toString(is);
		} catch (Exception e) {
			logger.catching(e);
		} finally {
			IOUtils.closeQuietly(is);
		}

		return null;
	}

	/**
	 * Take a file and return a hash. If an error occurs, null is returned.
	 *
	 * @return hashed password
	 * @throws IOException
	 */
	public static String hashFile(File file) throws IOException {
		if (file == null) {
			return null;
		}

		return SecurityUtil.hashByteArray(ResourcesUtil.fileToByteArray(file));
	}
}
