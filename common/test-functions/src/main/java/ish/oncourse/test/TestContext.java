/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.test;

import ish.oncourse.test.functions.Functions;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: akoiro
 * Date: 30/10/17
 */
public class TestContext {
	private static final Logger logger = LogManager.getLogger();
	public static final String SHOULD_CREATE_SCHEMA = "createSchema";
	public static final String SHOULD_DROP_SCHEMA = "dropSchema";


	private Map<String, Boolean> params = new HashMap<>();

	private BasicDataSource dataSource;
	private MariaDB mariaDB;

	public TestContext params(Map<String, Boolean> params) {
		if (params != null) {
			this.params = params;
		}
		return this;
	}

	public TestContext init() {
		mariaDB = MariaDB.valueOf();
		dataSource = Functions.createDS(mariaDB);
		Functions.initJNDI(dataSource);
		Boolean createSchema = params.get(SHOULD_CREATE_SCHEMA);
		if (createSchema == null || createSchema) {
			new CreateTables(Functions.createRuntime(), params).create();
		}
		return this;
	}

	public BasicDataSource getDS() {
		return dataSource;
	}
	
	public void cleanInsert(String dataSetResource) throws DatabaseUnitException, SQLException {
		InputStream st = TestContext.class.getClassLoader().getResourceAsStream(dataSetResource);
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		Functions.cleanDB(mariaDB,false);
		DatabaseConnection dbConnection = new DatabaseConnection(dataSource.getConnection(), null);
		dbConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
		dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
		DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
	}
	
	public void close() {
		try {
			dataSource.close();
		} catch (SQLException e) {
			logger.error(e);
		}

		Boolean dropSchema = params.get(SHOULD_DROP_SCHEMA);
		Functions.cleanDB(mariaDB,dropSchema == null || dropSchema);

	}
}
