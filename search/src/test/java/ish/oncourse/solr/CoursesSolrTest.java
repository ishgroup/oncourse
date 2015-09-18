/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr;

import ish.oncourse.test.ContextUtils;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CoursesSolrTest extends SolrTest {
	
	@BeforeClass
	public static void setupAll() throws Exception {
		initSolr("src/main/resources/solr", "courses");
	}

	@Before
	public void setup() throws Exception {
		InputStream st = CoursesSolrTest.class.getClassLoader().getResourceAsStream("ish/oncourse/solr/SolrCourseCoreTestDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource dataSource = ContextUtils.getDataSource("jdbc/oncourse");
		DatabaseConnection dbConnection = new DatabaseConnection(dataSource.getConnection(), null);
		dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

		DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
	}
	
	@Test
	public void testFullImport() throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("command", "full-import");

		h.query("/dataimport", new LocalSolrQueryRequest(h.getCore(), new MapSolrParams(params)));

		// FIXME: properly parse XML response to determine if importing has finished
		Thread.sleep(20000);

		h.query("/dataimport", new LocalSolrQueryRequest(h.getCore(), new MapSolrParams(params)));
	}
}
