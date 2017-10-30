/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.test;

import ish.oncourse.test.functions.Functions;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: akoiro
 * Date: 30/10/17
 */
public class TestContext {
	private static final Logger logger = LogManager.getLogger();

	private Map<String, Boolean> params;

	private BasicDataSource dataSource;
	private MariaDB mariaDB;

	public TestContext params(Map<String, Boolean> params) {
		this.params = params;
		return this;
	}

	public TestContext init() {
		mariaDB = MariaDB.valueOf();
		dataSource = Functions.createDS(mariaDB);
		Functions.initJNDI(dataSource);
		new CreateTables(Functions.createRuntime(), params).create();
		return this;
	}

	public BasicDataSource getDS() {
		return dataSource;
	}

	public void close() {
		try {
			dataSource.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		Functions.cleanDB(mariaDB);

	}
}
