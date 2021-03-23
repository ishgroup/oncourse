/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.test;

import ish.oncourse.test.functions.Functions;
import ish.oncourse.test.functions.ServerRuntimeBuilder;
import org.apache.cayenne.cache.QueryCache;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestContext {
	private static final Logger logger = LogManager.getLogger();

	public static final String SHOULD_CREATE_TABLES = "shouldCreateTables";

	private boolean shouldCreateTables = false;
	private boolean shouldCleanTables = true;
	private BasicDataSource dataSource;
	private MariaDB mariaDB;
	private ServerRuntime serverRuntime;
	private QueryCache queryCache;

	public TestContext() {
		initParams();
	}

	public TestContext queryCache(QueryCache queryCache) {
		this.queryCache = queryCache;
		return this;
	}

	public TestContext shouldCreateTables(boolean value) {
		this.shouldCreateTables = value;
		return this;
	}

	public TestContext shouldCleanTables(boolean value) {
		this.shouldCleanTables = value;
		return this;
	}


	public TestContext mariaDB(MariaDB mariaDB) {
		this.mariaDB = mariaDB;
		return this;
	}

	public TestContext serverRuntime(ServerRuntime serverRuntime) {
		this.serverRuntime = serverRuntime;
		return this;
	}


	public TestContext open() {
		try {
			if (mariaDB == null) {
				mariaDB = MariaDB.valueOf();
			}

			dataSource = Functions.createDS(mariaDB);
			Functions.bindDS(dataSource);

			if (shouldCreateTables) {
				Functions.createIfNotExistsDB(mariaDB);
				Functions.cleanDB(mariaDB, true);
			} else {
				if (shouldCleanTables)
					Functions.cleanDB(mariaDB, false);
			}

			if (serverRuntime == null)
				serverRuntime = new ServerRuntimeBuilder().queryCache(queryCache).build();

			if (shouldCreateTables)
				new CreateTables(serverRuntime).create();
			return this;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	private void initParams() {
		if (System.getProperty(SHOULD_CREATE_TABLES) != null) {
			shouldCreateTables = Boolean.valueOf(System.getProperty(SHOULD_CREATE_TABLES));
		}
	}

	public BasicDataSource getDS() {
		return dataSource;
	}

	public MariaDB getMariaDB() {
		return mariaDB;
	}

	public void close() {
		try {
			if (serverRuntime != null)
				this.serverRuntime.shutdown();
		} catch (Exception e) {
			logger.error(e);
		}

		try {
			dataSource.close();
		} catch (Exception e) {
			logger.error(e);
		}

		try {
			Functions.unbindDS();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void close(boolean shouldDropDB) {
		if (shouldDropDB)
			Functions.dropDB(mariaDB);
		close();
	}

	public ServerRuntime getServerRuntime() {
		return serverRuntime;
	}
}
