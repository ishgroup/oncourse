package ish.oncourse.model;

import ish.oncourse.test.ContextUtils;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CourseTest {
	private ObjectContext context;

	@Before
	public void setup() throws Exception {
		ContextUtils.setupDataSources();

		context = ContextUtils.createObjectContext();
	}


	@Test
	public void test_getRelatedToCourses()
	{
		College college = context.newObject(College.class);
		college.setName("name");
		college.setTimeZone("Australia/Sydney");
		college.setFirstRemoteAuthentication(new Date());
		college.setRequiresAvetmiss(false);

		Course course1 = context.newObject(Course.class);
		course1.setIsWebVisible(Boolean.TRUE);
		course1.setCollege(college);

		Course course2 = context.newObject(Course.class);
		course2.setIsWebVisible(Boolean.TRUE);
		course2.setCollege(college);

		List<Course> courseList = course1.getRelatedToCourses();
		assertNotNull(courseList);
		assertEquals(0, courseList.size());

		courseList = course2.getRelatedToCourses();
		assertNotNull(courseList);
		assertEquals(0, courseList.size());

		CourseCourseRelation courseRelation = context.newObject(CourseCourseRelation.class);
		courseRelation.setCollege(college);
		courseRelation.setFromCourse(course2);
		courseRelation.setToCourse(course1);

		courseList = course1.getRelatedToCourses();
		assertNotNull(courseList);
		assertEquals(1, courseList.size());

		courseList = course2.getRelatedToCourses();
		assertNotNull(courseList);
		assertEquals(1, courseList.size());

	}

}
