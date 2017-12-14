package ish.oncourse.services.course;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.courseclass.ClassAge;
import ish.oncourse.services.courseclass.ClassAgeType;
import ish.oncourse.services.preference.Preferences;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.Select;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetEnrollableClassesTest {

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
		Course course = createMockCourse();

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

	private Course createMockCourse() {
		Course course = mock(Course.class);

		List<CourseClass> courseClasses = new ArrayList<>();

		courseClasses.add(createMockCourseClass(createDateWithOffset(0, 0, -3),
				createDateWithOffset(0, 0, -2)));
		courseClasses.add(createMockCourseClass(createDateWithOffset(0, 0, -2),
				createDateWithOffset(0, 0, -1)));
		courseClasses.add(createMockCourseClass(createDateWithOffset(0, 0, -1),
				createDateWithOffset(0, 0, 0)));
		courseClasses.add(createMockCourseClass(createDateWithOffset(0, 0, 0),
				createDateWithOffset(0, 0, 1)));
		courseClasses.add(createMockCourseClass(createDateWithOffset(0, 0, 1),
				createDateWithOffset(0, 0, 2)));
		courseClasses.add(createMockCourseClass(createDateWithOffset(0, 0, 2),
				createDateWithOffset(0, 0, 3)));

		when(course.getCourseClasses()).thenReturn(courseClasses);

		return course;
	}

	private CourseClass createMockCourseClass(Date startClassDate, Date endClassDate) {
		CourseClass courseClass = mock(CourseClass.class);

		when(courseClass.getIsWebVisible()).thenReturn(true);
		when(courseClass.isCancelled()).thenReturn(false);
		when(courseClass.isHasAvailableEnrolmentPlaces()).thenReturn(true);
		when(courseClass.getStartDate()).thenReturn(startClassDate);
		when(courseClass.getEndDate()).thenReturn(endClassDate);

		return courseClass;
	}

	private Date createDateWithOffset(int yearOffset, int monthOffset, int dayOffset) {
		Date currentSystemDate = new Date();

		currentSystemDate = DateUtils.addYears(currentSystemDate, yearOffset);
		currentSystemDate = DateUtils.addMonths(currentSystemDate, monthOffset);
		currentSystemDate = DateUtils.addDays(currentSystemDate, dayOffset);
		return currentSystemDate;
	}

	private void assertEnrollableCourseClassCount(int expected, Course course, int dayCount, ClassAgeType ageType) {
		List<CourseClass> courseClasses = GetEnrollableClasses.valueOf(course)
				.classAge(() -> ClassAge.valueOf(dayCount, ageType))
				.get();
		assertEquals(expected, courseClasses.size());
	}
}
