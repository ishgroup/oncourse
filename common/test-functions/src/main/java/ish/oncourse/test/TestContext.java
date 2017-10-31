/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.test;

import ish.oncourse.test.functions.Functions;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

import java.io.InputStream;
import java.sql.SQLException;

/**
 * User: akoiro
 * Date: 30/10/17
 */
public class TestContext {
	private static final Logger logger = LogManager.getLogger();

	public static final String SHOULD_CREATE_TABLES = "shouldCreateTables";
	public static final String SHOULD_CREATE_FK_CONSTRAINTS = "shouldCreateFKConstraints";


	private boolean shouldCreateTables = false;
	private boolean shouldCreateFKConstraints = true;

	private BasicDataSource dataSource;
	private MariaDB mariaDB;

	public TestContext shouldCreateFKConstraints(boolean value) {
		this.shouldCreateFKConstraints = value;
		return this;
	}

	public TestContext shouldCreateTables(boolean value) {
		this.shouldCreateTables = value;
		return this;
	}

	public TestContext mariaDB(MariaDB mariaDB) {
		this.mariaDB = mariaDB;
		return this;
	}

	public TestContext open() {
		if (mariaDB == null) {
			mariaDB = MariaDB.valueOf();
		}
		initParams();

		dataSource = Functions.createDS(mariaDB);
		Functions.bindDS(dataSource);
		if (shouldCreateTables) {
			Functions.cleanDB(mariaDB, true);
			new CreateTables(Functions.createRuntime()).shouldCreateFKConstraints(shouldCreateFKConstraints).create();
		} else {
			Functions.cleanDB(mariaDB, false);
		}
		return this;
	}

	private void initParams() {
		if (System.getProperty(SHOULD_CREATE_TABLES) != null) {
			shouldCreateTables = Boolean.valueOf(System.getProperty(SHOULD_CREATE_TABLES));
		}
		if (System.getProperty(SHOULD_CREATE_FK_CONSTRAINTS) != null) {
			shouldCreateFKConstraints = Boolean.valueOf(System.getProperty(SHOULD_CREATE_FK_CONSTRAINTS));
		}
	}

	public BasicDataSource getDS() {
		return dataSource;
	}

	public MariaDB getMariaDB() {
		return mariaDB;
	}

	public void cleanInsert(String dataSetResource) throws Exception {
		InputStream st = TestContext.class.getClassLoader().getResourceAsStream(dataSetResource);
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DatabaseConnection dbConnection = new DatabaseConnection(dataSource.getConnection());
		dbConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
		dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
		DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
	}

	public void close() {
		try {
			dataSource.close();
			Functions.unbindDS();
		} catch (SQLException e) {
			logger.error(e);
		}
	}
}
