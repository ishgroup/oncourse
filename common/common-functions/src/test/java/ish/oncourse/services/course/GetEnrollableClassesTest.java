package ish.oncourse.services.course;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.courseclass.ClassAge;
import ish.oncourse.services.courseclass.ClassAgeType;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.Preferences;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.TestContext;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.Select;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang.time.DateUtils.addDays;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetEnrollableClassesTest {

	protected TestContext testContext;

	@Before
	public void setup() throws Exception {
		testContext = new TestContext().open();

		new LoadDataSet().dataSetFile("ish/oncourse/services/course/GetEnrollableClassesTestDataSet.xml")
				.load(testContext.getDS());

	}

	@Test
	public void test_STOP_WEB_ENROLMENTS_Preferences() {
		ObjectContext context = mock(ObjectContext.class);
		Course course = mock(Course.class);
		when(course.getObjectContext()).thenReturn(context);
		GetEnrollableClasses.valueOf(course).get();
		Mockito.verify(context,
				data -> {
					Assert.assertTrue(((ObjectSelect) data.getAllInvocations().get(0).getRawArguments()[0]).getWhere().toEJBQL("p").contains(Preferences.STOP_WEB_ENROLMENTS_AGE));
					Assert.assertTrue(((ObjectSelect) data.getAllInvocations().get(1).getRawArguments()[0]).getWhere().toEJBQL("p").contains(Preferences.STOP_WEB_ENROLMENTS_AGE_TYPE));
				}).selectOne(Mockito.any(Select.class));
	}

	@Test
	public void test() {
		Course course =createCourse();
		
		assertEnrollableCourseClassCount(3, course, 0, ClassAgeType.beforeClassStarts);
		assertEnrollableCourseClassCount(2, course, 1, ClassAgeType.beforeClassStarts);
		assertEnrollableCourseClassCount(1, course, 2, ClassAgeType.beforeClassStarts);
		assertEnrollableCourseClassCount(0, course, 3, ClassAgeType.beforeClassStarts);
		assertEnrollableCourseClassCount(0, course, 4, ClassAgeType.beforeClassStarts);

		assertEnrollableCourseClassCount(3, course, 0, ClassAgeType.afterClassStarts);
		assertEnrollableCourseClassCount(4, course, 1, ClassAgeType.afterClassStarts);
		assertEnrollableCourseClassCount(5, course, 2, ClassAgeType.afterClassStarts);
		assertEnrollableCourseClassCount(6, course, 3, ClassAgeType.afterClassStarts);
		assertEnrollableCourseClassCount(6, course, 4, ClassAgeType.afterClassStarts);

		assertEnrollableCourseClassCount(4, course, 0, ClassAgeType.beforeClassEnds);
		assertEnrollableCourseClassCount(3, course, 1, ClassAgeType.beforeClassEnds);
		assertEnrollableCourseClassCount(2, course, 2, ClassAgeType.beforeClassEnds);
		assertEnrollableCourseClassCount(1, course, 3, ClassAgeType.beforeClassEnds);
		assertEnrollableCourseClassCount(0, course, 4, ClassAgeType.beforeClassEnds);
		assertEnrollableCourseClassCount(0, course, 5, ClassAgeType.beforeClassEnds);

		assertEnrollableCourseClassCount(4, course, 0, ClassAgeType.afterClassEnds);
		assertEnrollableCourseClassCount(5, course, 1, ClassAgeType.afterClassEnds);
		assertEnrollableCourseClassCount(6, course, 2, ClassAgeType.afterClassEnds);
		assertEnrollableCourseClassCount(6, course, 3, ClassAgeType.afterClassEnds);
	}

	private Course createCourse() {
		ObjectContext context = testContext.getServerRuntime().newContext();
		Course course = ObjectSelect.query(Course.class).selectFirst(context);
		
		Date currentDate = new Date();

		createCourseClass(course, addDays(currentDate, -3), addDays(currentDate, -2));
		createCourseClass(course, addDays(currentDate, -2), addDays(currentDate, -1));
		createCourseClass(course, addDays(currentDate, -1), currentDate);
		createCourseClass(course, currentDate, addDays(currentDate, 1));
		createCourseClass(course, addDays(currentDate, 1), addDays(currentDate, 2));
		createCourseClass(course, addDays(currentDate, 2), addDays(currentDate, 3));

		context.commitChanges();
		return course;
	}

	private void createCourseClass(Course course, Date startClassDate, Date endClassDate) {
		CourseClass courseClass = course.getObjectContext().newObject(CourseClass.class);
		courseClass.setCourse(course);
		courseClass.setCollege(course.getCollege());
		courseClass.setIsDistantLearningCourse(false);
		courseClass.setIsWebVisible(true);
		courseClass.setIsActive(true);
		courseClass.setCancelled(false);
		courseClass.setMaximumPlaces(999);
		courseClass.setStartDate(startClassDate);
		courseClass.setEndDate(endClassDate);
	}


	private void assertEnrollableCourseClassCount(int expected, Course course, int dayCount, ClassAgeType ageType) {
		List<CourseClass> courseClasses = GetEnrollableClasses.valueOf(course)
				.classAge(() -> ClassAge.valueOf(dayCount, ageType))
				.get();
		assertEquals(expected, courseClasses.size());
	}
}
