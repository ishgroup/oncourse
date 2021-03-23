package ish.oncourse.services.course;

import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.courseclass.ClassAge;
import ish.oncourse.services.courseclass.ClassAgeType;
import ish.oncourse.services.preference.Preferences;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.TestContext;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.Select;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.List;

import static org.apache.commons.lang.time.DateUtils.addDays;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetWebVisibleClassesTest {

    protected TestContext testContext;

    @Before
    public void setup() throws Exception {
        testContext = new TestContext().open();

        new LoadDataSet().dataSetFile("ish/oncourse/services/course/GetWebVisibleClassesTestDataSet.xml")
                .load(testContext.getDS());

    }

    @Test
    public void test_HIDE_CLASS_ON_WEB_Preferences() {
        ObjectContext context = mock(ObjectContext.class);
        College college = mock(College.class);
        when(college.getObjectContext()).thenReturn(context);

        Course course = mock(Course.class);
        when(course.getCollege()).thenReturn(college);
        when(course.getObjectContext()).thenReturn(context);
        when(course.getCollege()).thenReturn(college);

        GetWebVisibleClasses.valueOf(course).get();
        Mockito.verify(context,
                data -> {
                    Assert.assertTrue(((ObjectSelect) data.getAllInvocations().get(0).getRawArguments()[0]).getWhere().toEJBQL("p").contains(Preferences.HIDE_CLASS_ON_WEB_AGE));
                    Assert.assertTrue(((ObjectSelect) data.getAllInvocations().get(1).getRawArguments()[0]).getWhere().toEJBQL("p").contains(Preferences.HIDE_CLASS_ON_WEB_AGE_TYPE));
                }).selectOne(Mockito.any(Select.class));
    }

    @Test
    public void test() {
        Course course = createCourse();

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

    private Course createCourse() {

        ObjectContext context = testContext.getServerRuntime().newContext();
        Course course = ObjectSelect.query(Course.class).selectFirst(context);

        Date currentDate = new Date();

        createCourseClass(course, addDays(currentDate, -15), addDays(currentDate, -13), false);
        createCourseClass(course, addDays(currentDate, -1), addDays(currentDate, 1), true);
        createCourseClass(course, addDays(currentDate, 6), addDays(currentDate, 12), true);

        context.commitChanges();
        return course;
    }

    private void createCourseClass(Course course, Date startClassDate, Date endClassDate, boolean isHasAvailableEnrolmentPlace) {
        CourseClass courseClass = course.getObjectContext().newObject(CourseClass.class);
        courseClass.setCourse(course);
        courseClass.setCollege(course.getCollege());
        courseClass.setIsDistantLearningCourse(false);
        courseClass.setIsWebVisible(true);
        courseClass.setIsActive(true);
        courseClass.setCancelled(false);
        courseClass.setMaximumPlaces(isHasAvailableEnrolmentPlace? 999: 0);
        courseClass.setStartDate(startClassDate);
        courseClass.setEndDate(endClassDate);
    }

    private void assertWebVisibleCourseClassCount(int expected, Course course, int dayCount, ClassAgeType ageType) {
        List<CourseClass> courseClasses = GetWebVisibleClasses.valueOf(course)
                .classAge(() -> ClassAge.valueOf(dayCount, ageType))
                .get();
        assertEquals(expected, courseClasses.size());
    }

}
