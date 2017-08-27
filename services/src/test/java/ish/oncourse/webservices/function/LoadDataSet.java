/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.function;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * User: akoiro
 * Date: 27/8/17
 */
public class LoadDataSet {
	private String dataSetFile;
	private Map<Object, Object> replacements;
	private ReplacementDataSet dataSet;

	public LoadDataSet(String dataSetFile, Map<Object, Object> replacements) {
		this.dataSetFile = dataSetFile;
		this.replacements = replacements == null ? Collections.emptyMap() : replacements;
	}

	public LoadDataSet load(DataSource dataSource) {
		try {
			InputStream st = LoadDataSet.class.getClassLoader().getResourceAsStream(dataSetFile);
			this.dataSet = initDataSet(st);
			DatabaseConnection dbConnection = new DatabaseConnection(dataSource.getConnection());
			dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
			DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	private ReplacementDataSet initDataSet(InputStream st) throws DataSetException {
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
		replacements.forEach(replacementDataSet::addReplacementObject);
		return replacementDataSet;
	}
}
