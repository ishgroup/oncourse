/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.test;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: akoiro
 * Date: 27/8/17
 */
public class LoadDataSet {
	private String dataSetFile;
	private Map<Object, Object> replacements = new HashMap<>();
	private boolean clean = true;

	private ReplacementDataSet dataSet;

	public LoadDataSet dataSetFile(String dataSetFile) {
		this.dataSetFile = dataSetFile;
		return this;
	}

	public LoadDataSet addReplacement(Object key, Object value) {
		this.replacements.put(key, value);
		return this;
	}

	public LoadDataSet replacements(Map<Object, Object> replacements) {
		if (replacements != null) {
			this.replacements.putAll(replacements);
		}
		return this;
	}

	public LoadDataSet clean(boolean clean) {
		this.clean = clean;
		return this;
	}

	public LoadDataSet load(DataSource dataSource) {
		try (Connection connection = dataSource.getConnection()) {
			return load(connection);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public LoadDataSet load(Connection connection) {
		try {
			InputStream st = LoadDataSet.class.getClassLoader().getResourceAsStream(dataSetFile);
			this.dataSet = initDataSet(st);
			DatabaseConnection dbConnection = createDatabaseConnection(connection);
			if (clean)
				DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
			else
				DatabaseOperation.INSERT.execute(dbConnection, dataSet);
			connection.commit();
		} catch (SQLException | DatabaseUnitException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public static DatabaseConnection createDatabaseConnection(Connection connection) {
		try {
			DatabaseConnection dbConnection = new DatabaseConnection(connection);
			dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
			dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
			dbConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
			return dbConnection;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private ReplacementDataSet initDataSet(InputStream st) throws DataSetException {
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).build(st);
		ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
		replacements.forEach(replacementDataSet::addReplacementObject);
		return replacementDataSet;
	}
}
