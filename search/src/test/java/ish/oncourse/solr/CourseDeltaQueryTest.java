package ish.oncourse.solr;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Session;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.util.XMLErrorLogger;
import org.apache.solr.handler.dataimport.config.ConfigParseUtil;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class CourseDeltaQueryTest extends ServiceTest {
	private static Logger log = LoggerFactory.getLogger(CourseDeltaQueryTest.class);
	public static final String COURSES_CONF_ONCOURSE_JDBC_PATH = "src/main/resources/solr/courses/conf/oncourse-jdbc.xml";
	private ICayenneService cayenneService;
	private DataSource dataSource;

	@Before
	public void setupDataSet() throws Exception {
		initTest("ish.oncourse.solr", "search", SearchTestModule.class);

		InputStream st = CourseDeltaQueryTest.class.getClassLoader().getResourceAsStream("ish/oncourse/solr/SolrCourseCoreTestDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		dataSource = getDataSource("jdbc/oncourse");
		DatabaseConnection dbConnection = new DatabaseConnection(dataSource.getConnection(), null);
		dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

		DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
		cayenneService = getService(ICayenneService.class);
	}

	private String getDeltaQueryString() {
		File configFile = new File(COURSES_CONF_ONCOURSE_JDBC_PATH);
		XMLErrorLogger XMLLOG = new XMLErrorLogger(log);
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			try {
				dbf.setXIncludeAware(true);
				dbf.setNamespaceAware(true);
			} catch( UnsupportedOperationException e ) {
				log.warn( "XML parser doesn't support XInclude option" );
			}
			DocumentBuilder builder = dbf.newDocumentBuilder();
			builder.setErrorHandler(XMLLOG);

			Document document = builder.parse(configFile);

			NodeList dataConfigTags = document.getElementsByTagName("dataConfig");
			Element dataConfigElement = (Element) dataConfigTags.item(0);
			List<Element> documentTags = ConfigParseUtil.getChildNodes(dataConfigElement, "document");
			Element docElement = documentTags.get(0);
			List<Element> entities = ConfigParseUtil.getChildNodes(docElement, "entity");
			Element courseEntity = entities.get(0);
			return courseEntity.getAttribute("deltaQuery");
		} catch (Exception e) {
			assertFalse(true);
			return StringUtils.EMPTY;
		}
	}

	@Test
	public void testMatchCourseWithFinishedClass() {
		String deltaQuery = getDeltaQueryString();
		String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String optimizedQuery = deltaQuery.replaceAll("'\\$\\{dataimporter.last_index_time\\}'", ("TIMESTAMP('"+currentTime +"')"));
		//check that delta query match only finished class
		executeDeltaQuery(optimizedQuery, Arrays.asList(10l));
	}

	@Test
	public void testModifyClass() {
		ObjectContext context = cayenneService.newContext();
		String deltaQuery = getDeltaQueryString();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, -1);
		String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		String optimizedQuery = deltaQuery.replaceAll("'\\$\\{dataimporter.last_index_time\\}'", ("TIMESTAMP('"+currentTime +"')"));
		SelectQuery selectQuery = new SelectQuery(CourseClass.class, ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 12l));
		List<CourseClass> courseClasses = context.performQuery(selectQuery);
		assertTrue(courseClasses.size() == 1);
		CourseClass courseClass = courseClasses.get(0);
		calendar = Calendar.getInstance();
		courseClass.setModified(calendar.getTime());
		context.commitChanges();

		//check that both finished and modified class will pass to delta query
		executeDeltaQuery(optimizedQuery, Arrays.asList(10l, 11l));
		//check that after delta import query will return only finished class
		calendar.add(Calendar.SECOND, 1);
		currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		optimizedQuery = deltaQuery.replaceAll("'\\$\\{dataimporter.last_index_time\\}'", ("TIMESTAMP('"+currentTime +"')"));
		executeDeltaQuery(optimizedQuery, Arrays.asList(10l));
	}

	@Test
	public void testModifyCourse() {
		ObjectContext context = cayenneService.newContext();
		String deltaQuery = getDeltaQueryString();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, -1);
		String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		String optimizedQuery = deltaQuery.replaceAll("'\\$\\{dataimporter.last_index_time\\}'", ("TIMESTAMP('"+currentTime +"')"));
		//update course
		SelectQuery selectQuery = new SelectQuery(Course.class, ExpressionFactory.matchDbExp(Course.ID_PK_COLUMN, 11l));
		List<Course> courses = context.performQuery(selectQuery);
		assertTrue(courses.size() == 1);
		Course course = courses.get(0);
		calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 1);
		course.setModified(calendar.getTime());
		context.commitChanges();

		//check that both finished and modified class will pass to delta query
		executeDeltaQuery(optimizedQuery, Arrays.asList(10l, 11l));
		//check that after delta import query will return only finished class
		calendar.add(Calendar.SECOND, 1);
		currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		optimizedQuery = deltaQuery.replaceAll("'\\$\\{dataimporter.last_index_time\\}'", ("TIMESTAMP('"+currentTime +"')"));
		executeDeltaQuery(optimizedQuery, Arrays.asList(10l));
	}

	@Test
	public void testModifyEnrolment() {
		ObjectContext context = cayenneService.newContext();
		String deltaQuery = getDeltaQueryString();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, -1);
		String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		String optimizedQuery = deltaQuery.replaceAll("'\\$\\{dataimporter.last_index_time\\}'", ("TIMESTAMP('"+currentTime +"')"));
		//update enrolment
		SelectQuery selectQuery = new SelectQuery(Enrolment.class, ExpressionFactory.matchDbExp(Enrolment.ID_PK_COLUMN, 10l));
		List<Enrolment> enrolments = context.performQuery(selectQuery);
		assertTrue(enrolments.size() == 1);
		Enrolment enrolment = enrolments.get(0);
		calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 1);
		enrolment.setModified(calendar.getTime());
		context.commitChanges();

		//check that both finished and modified enrolment class will pass to delta query
		executeDeltaQuery(optimizedQuery, Arrays.asList(10l, 11l));
		//check that after delta import query will return only finished class
		calendar.add(Calendar.SECOND, 1);
		currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		optimizedQuery = deltaQuery.replaceAll("'\\$\\{dataimporter.last_index_time\\}'", ("TIMESTAMP('"+currentTime +"')"));
		executeDeltaQuery(optimizedQuery, Arrays.asList(10l));
	}

	@Test
	public void testModifySession() {
		ObjectContext context = cayenneService.newContext();
		String deltaQuery = getDeltaQueryString();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, -1);
		String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		String optimizedQuery = deltaQuery.replaceAll("'\\$\\{dataimporter.last_index_time\\}'", ("TIMESTAMP('"+currentTime +"')"));
		//update session
		SelectQuery selectQuery = new SelectQuery(Session.class, ExpressionFactory.matchDbExp(Session.ID_PK_COLUMN, 11l));
		List<Session> sessions = context.performQuery(selectQuery);
		assertTrue(sessions.size() == 1);
		Session session = sessions.get(0);
		calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 1);
		session.setModified(calendar.getTime());
		context.commitChanges();

		//check that both finished and modified session class will pass to delta query
		executeDeltaQuery(optimizedQuery, Arrays.asList(10l, 11l));
		//check that after delta import query will return only finished class
		calendar.add(Calendar.SECOND, 1);
		currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		optimizedQuery = deltaQuery.replaceAll("'\\$\\{dataimporter.last_index_time\\}'", ("TIMESTAMP('"+currentTime +"')"));
		executeDeltaQuery(optimizedQuery, Arrays.asList(10l));
	}

	private void executeDeltaQuery(String query, List<Long> expectedResults) {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = dataSource.getConnection();
			if (connection != null && !connection.isClosed()) {
				statement = connection.createStatement();
			} else {
				assertFalse(true);
			}
			ResultSet result = null;
			try {
				System.out.println(query);
				result = statement.executeQuery(query);
				if (result != null) {
					for (Long expectedCourseId : expectedResults) {
						assertTrue(result.next());
						assertEquals(expectedCourseId.longValue(), result.getLong(1));
					}
					try {
						if (result.next()) {
							assertFalse(String.format("No elements should left in result set but %s course included", result.getLong(1)), true);
						}
					} catch (SQLException e) {}
				}
			} finally {
				if (result != null && !result.isClosed()) {
					result.close();
				}
			}
		} catch (SQLException e) {
			log.warn("Failed to execute delta query because connection already closed", e);
		} finally {
			try {
				if (statement != null && !statement.isClosed()) {
					statement.close();
				}
			} catch (SQLException e) {
				log.warn("Exception occurs when try to close the statement", e);
			}
			try {
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				log.warn("Exception occurs when try to close the connection", e);
			}
		}
	}
}
