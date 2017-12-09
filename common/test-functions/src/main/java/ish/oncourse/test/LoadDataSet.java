/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.test;

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
import java.util.Collections;
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
		this.replacements = replacements == null ? Collections.emptyMap() : replacements;
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
		try {
			InputStream st = LoadDataSet.class.getClassLoader().getResourceAsStream(dataSetFile);
			this.dataSet = initDataSet(st);
			DatabaseConnection dbConnection = new DatabaseConnection(dataSource.getConnection());
			dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
			dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
			dbConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
			if (clean)
				DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
			else
				DatabaseOperation.INSERT.execute(dbConnection, dataSet);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	private ReplacementDataSet initDataSet(InputStream st) throws DataSetException {
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).build(st);
		ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
		replacements.forEach(replacementDataSet::addReplacementObject);
		return replacementDataSet;
	}
}
