/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.util.log;

import ish.oncourse.configuration.ApplicationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * User: akoiro
 * Date: 29/8/17
 */
public class LogAppInfo {
	private final static Logger LOGGER = LogManager.getLogger();

	public void log(DataSource dataSource) {
		try (Connection connection = dataSource.getConnection()) {
			LOGGER.info(String.format("DB Address: %s", getHost(connection.getMetaData())));
			connection.close();
			LOGGER.info(String.format("Application deployed version: %s", ApplicationUtils.getAppVersion()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
