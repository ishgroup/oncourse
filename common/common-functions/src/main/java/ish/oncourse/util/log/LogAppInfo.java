/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.util.log;

import ish.oncourse.configuration.ApplicationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * User: akoiro
 * Date: 29/8/17
 */
public class LogAppInfo {
	public final static String DATA_SOURSE_NAME = "willow";
	private final static Logger LOGGER = LogManager.getLogger();
	
	private DataSource dataSource;
	
	public LogAppInfo(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void log() {
		logDataSource();
		logVersion();
		logDefaultCharset();
	}
	
	private void logDataSource() {
		try (Connection connection = dataSource.getConnection()) {
			LOGGER.info(String.format("DB Address: %s", getHost(connection.getMetaData())));
			connection.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void logVersion() {
		LOGGER.info(String.format("Application Version: %s", ApplicationUtils.getAppVersion()));
	}

	private void logDefaultCharset() {
		LOGGER.info(String.format("Default Charset: %s", Charset.defaultCharset().name()));
	}


	private String getHost(DatabaseMetaData md) throws SQLException {
		try {
			Connection connection = md.getConnection();
			return (String) connection.getClass().getMethod("getHost").invoke(connection);
		} catch (Exception e) {
			return md.getURL();
		}
	}
}
