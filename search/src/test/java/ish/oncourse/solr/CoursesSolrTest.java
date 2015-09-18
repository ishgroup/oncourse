/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr;

import com.google.gson.Gson;
import ish.common.types.CourseEnrolmentType;
import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.services.search.SearchParams;
import ish.oncourse.services.search.SolrQueryBuilder;
import ish.oncourse.test.ContextUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.solr.client.solrj.SolrQuery;
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
import java.util.Date;
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
		Gson gson = new Gson();

		Map<String, String> params = new HashMap<>();
		params.put("command", "full-import");
		params.put("wt", "json");

		h.query("/dataimport", new LocalSolrQueryRequest(h.getCore(), new MapSolrParams(params)));

		Map response = gson.fromJson(h.query("/dataimport", new LocalSolrQueryRequest(h.getCore(), new MapSolrParams(params))), Map.class);

		while ("busy".equals(response.get("status"))) {
			Thread.sleep(5000);
			response = gson.fromJson(h.query("/dataimport", new LocalSolrQueryRequest(h.getCore(), new MapSolrParams(params))), Map.class);
		}

		assertEquals("2", ((Map) response.get("statusMessages")).get("Total Documents Processed"));
	}

	@Test
	public void testDeltaImport() throws Exception {
		Gson gson = new Gson();

		Map<String, String> params = new HashMap<>();
		params.put("command", "full-import");
		params.put("wt", "json");

		h.query("/dataimport", new LocalSolrQueryRequest(h.getCore(), new MapSolrParams(params)));

		Map response = gson.fromJson(h.query("/dataimport", new LocalSolrQueryRequest(h.getCore(), new MapSolrParams(params))), Map.class);

		while ("busy".equals(response.get("status"))) {
			Thread.sleep(5000);
			response = gson.fromJson(h.query("/dataimport", new LocalSolrQueryRequest(h.getCore(), new MapSolrParams(params))), Map.class);
		}

		assertEquals("2", ((Map) response.get("statusMessages")).get("Total Documents Processed"));

		SolrQuery query = SolrQueryBuilder.valueOf(new SearchParams(), "10", 0, 10).build().addFilterQuery("course_code: AAV");
		response = gson.fromJson(JQ(new LocalSolrQueryRequest(h.getCore(), query)), Map.class);

		assertEquals(1.0, ((Map) response.get("response")).get("numFound"));

		response = gson.fromJson(JQ(new LocalSolrQueryRequest(h.getCore(),
				SolrQueryBuilder.valueOf(new SearchParams(), "10", 0, 10).build().addFilterQuery("course_code: BBB"))),
				Map.class);

		assertEquals(0.0, ((Map) response.get("response")).get("numFound"));

		ObjectContext context = SearchContextUtils.createObjectContext();

		Date now = new Date();

		College college = Cayenne.objectForPK(context, College.class, 10);

		Course course = context.newObject(Course.class);
		course.setCollege(college);
		course.setCode("BBB");
		course.setName("BBB test course");
		course.setIsWebVisible(true);
		course.setIsVETCourse(false);
		course.setIsSufficientForQualification(false);
		course.setEnrolmentType(CourseEnrolmentType.OPEN_FOR_ENROLMENT);
		course.setCreated(now);
		course.setModified(now);

		context.commitChanges();

		params = new HashMap<>();
		params.put("command", "delta-import");
		params.put("wt", "json");

		h.query("/dataimport", new LocalSolrQueryRequest(h.getCore(), new MapSolrParams(params)));

		response = gson.fromJson(h.query("/dataimport", new LocalSolrQueryRequest(h.getCore(), new MapSolrParams(params))), Map.class);

		while ("busy".equals(response.get("status"))) {
			Thread.sleep(5000);
			response = gson.fromJson(h.query("/dataimport", new LocalSolrQueryRequest(h.getCore(), new MapSolrParams(params))), Map.class);
		}

		assertEquals("1", ((Map) response.get("statusMessages")).get("Total Documents Processed"));

		response = gson.fromJson(JQ(new LocalSolrQueryRequest(h.getCore(),
						SolrQueryBuilder.valueOf(new SearchParams(), "10", 0, 10).build().addFilterQuery("course_code: BBB"))),
				Map.class);

		assertEquals(1.0, ((Map) response.get("response")).get("numFound"));
	}
}
