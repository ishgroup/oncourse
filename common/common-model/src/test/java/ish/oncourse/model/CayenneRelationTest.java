/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.model;

import ish.common.types.CourseEnrolmentType;
import ish.oncourse.test.TestContext;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Ignore;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Test Course->CourseCourseRelation->Course
 * The test was whiten for task 29007 and is not ready to use on jenkins
 *
 * The test shows an issue in the cayenne relation handling.
 * See details in task 29007
 *
 * User: akoiro
 * Date: 15/06/2016
 */
public class CayenneRelationTest {
	private TestContext testContext = new TestContext();
	private ObjectContext context;
	private  College college;

	@Before
	public void setup() throws Exception {
		testContext.open();
		context = testContext.getServerRuntime().newContext();
		college = createCollege();

	}

	@Ignore
	public void test() throws NamingException, SQLException {

		Course fromCourse = createCourse();

		Course toCourse = createCourse();

		CourseCourseRelation relation = context.newObject(CourseCourseRelation.class);
		relation.setFromCourse(fromCourse);
		relation.setToCourse(toCourse);
		relation.setCollege(college);

		Course toCourse1 = createCourse();

		context.commitChanges();

		String sql = String.format("INSERT INTO EntityRelation (collegeId, fromEntityIdentifier, fromRecordId, toEntityIdentifier, toRecordId) VALUES ( %d, 1, %d, 1, %d);\n" +
				"DELETE FROM EntityRelation where id = %d",
				college.getId(), fromCourse.getId(), toCourse1.getId(), relation.getId());

		fromCourse = SelectById.query(Course.class, fromCourse.getId()).selectOne(context);
		assertEquals(1, fromCourse.getToCourses().size());
		assertEquals(toCourse, fromCourse.getToCourses().get(0).getToCourse());

		execute(sql);

		fromCourse = SelectById.query(Course.class, fromCourse.getId()).selectOne(context);
		ObjectSelect.query(CourseCourseRelation.class).select(context);
		assertEquals(1, fromCourse.getToCourses().size());
		assertEquals(toCourse1, fromCourse.getToCourses().get(0).getToCourse());
	}

	private void execute(String sql) throws NamingException, SQLException {
		try {
			DataSource dataSource = InitialContext.doLookup("jdbc/oncourse");
			Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement();

			String[] sqls = sql.split(";");
			for (String sql1 : sqls) {
				statement.execute(sql1);
			}
			statement.close();
			connection.commit();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private College createCollege() {
		College college = context.newObject(College.class);
		college.setCollegeKey(RandomStringUtils.random(5, true, true));
		college.setName(RandomStringUtils.random(10, true, true));
		college.setTimeZone("Australia/Sydney");
		college.setRequiresAvetmiss(Boolean.FALSE);
		college.setFirstRemoteAuthentication(new Date());
		return college;
	}

	private Course createCourse() {
		Course course = context.newObject(Course.class);
		course.setCode(RandomStringUtils.random(5, true, true));
		course.setName(RandomStringUtils.random(10, true, true));
		course.setCollege(college);
		course.setEnrolmentType(CourseEnrolmentType.OPEN_FOR_ENROLMENT);
		course.setIsWebVisible(Boolean.TRUE);
		return course;
	}
}
