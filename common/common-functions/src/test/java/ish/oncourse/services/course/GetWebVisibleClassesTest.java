package ish.oncourse.services.course;


import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.courseclass.ClassAge;
import ish.oncourse.services.courseclass.ClassAgeType;
import ish.oncourse.services.preference.Preferences;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.Select;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang.time.DateUtils.addDays;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetWebVisibleClassesTest {

    @Test
    public void test_HIDE_CLASS_ON_WEB_Preferences() {
        ObjectContext context = mock(ObjectContext.class);
        Course course = mock(Course.class);
        when(course.getObjectContext()).thenReturn(context);
        GetWebVisibleClasses.valueOf(course).get();
        Mockito.verify(context,
                data -> {
                    Assert.assertTrue(((ObjectSelect) data.getAllInvocations().get(0).getRawArguments()[0]).getWhere().toEJBQL("p").contains(Preferences.HIDE_CLASS_ON_WEB_AGE));
                    Assert.assertTrue(((ObjectSelect) data.getAllInvocations().get(1).getRawArguments()[0]).getWhere().toEJBQL("p").contains(Preferences.HIDE_CLASS_ON_WEB_AGE_TYPE));
                }).selectOne(Mockito.any(Select.class));
    }

    @Test
    public void test() {
        Course course = createMockCourse();

        assertWebVisibleCourseClassCount(0, course, 16, ClassAgeType.beforeClassStarts);
        assertWebVisibleCourseClassCount(0, course, 14, ClassAgeType.beforeClassStarts);
        assertWebVisibleCourseClassCount(0, course, 7, ClassAgeType.beforeClassStarts);
        assertWebVisibleCourseClassCount(1, course, 0, ClassAgeType.beforeClassStarts);

        assertWebVisibleCourseClassCount(3, course, 16, ClassAgeType.afterClassStarts);
        assertWebVisibleCourseClassCount(2, course, 14, ClassAgeType.afterClassStarts);
        assertWebVisibleCourseClassCount(2, course, 7, ClassAgeType.afterClassStarts);
        assertWebVisibleCourseClassCount(1, course, 0, ClassAgeType.afterClassStarts);

        assertWebVisibleCourseClassCount(2, course, 0, ClassAgeType.beforeClassEnds);
        assertWebVisibleCourseClassCount(1, course, 3, ClassAgeType.beforeClassEnds);
        assertWebVisibleCourseClassCount(1, course, 7, ClassAgeType.beforeClassEnds);
        assertWebVisibleCourseClassCount(1, course, 8, ClassAgeType.beforeClassEnds);

        assertWebVisibleCourseClassCount(2, course, 0, ClassAgeType.afterClassEnds);
        assertWebVisibleCourseClassCount(2, course, 3, ClassAgeType.afterClassEnds);
        assertWebVisibleCourseClassCount(2, course, 7, ClassAgeType.afterClassEnds);
        assertWebVisibleCourseClassCount(2, course, 8, ClassAgeType.afterClassEnds);
    }

    private Course createMockCourse() {
        Course course = mock(Course.class);

        List<CourseClass> courseClasses = new ArrayList<>();
        Date currentDate = new Date();

        courseClasses.add(createMockCourseClass(addDays(currentDate, -15), addDays(currentDate, -13), false));
        courseClasses.add(createMockCourseClass(addDays(currentDate, -1), addDays(currentDate, 1), true));
        courseClasses.add(createMockCourseClass(addDays(currentDate, 6), addDays(currentDate, 12), true));

        when(course.getCourseClasses()).thenReturn(courseClasses);

        return course;
    }

    private CourseClass createMockCourseClass(Date startClassDate, Date endClassDate, boolean isHasAvailableEnrolmentPlace) {
        CourseClass courseClass = mock(CourseClass.class);

        when(courseClass.getIsWebVisible()).thenReturn(true);
        when(courseClass.isCancelled()).thenReturn(false);
        when(courseClass.isHasAvailableEnrolmentPlaces()).thenReturn(isHasAvailableEnrolmentPlace);
        when(courseClass.getStartDate()).thenReturn(startClassDate);
        when(courseClass.getEndDate()).thenReturn(endClassDate);

        return courseClass;
    }

    private void assertWebVisibleCourseClassCount(int expected, Course course, int dayCount, ClassAgeType ageType) {
        List<CourseClass> courseClasses = GetWebVisibleClasses.valueOf(course)
                .classAge(() -> ClassAge.valueOf(dayCount, ageType))
                .get();
        assertEquals(expected, courseClasses.size());
    }

}
