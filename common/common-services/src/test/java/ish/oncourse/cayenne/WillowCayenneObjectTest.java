package ish.oncourse.cayenne;

import ish.common.types.CourseEnrolmentType;
import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Language;
import ish.oncourse.test.TestContext;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.junit.Before;
import org.junit.Test;


import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import static org.testng.Assert.assertEquals;

public class WillowCayenneObjectTest {

    private TestContext testContext;

    @Before
    public void prepare() {
        testContext = new TestContext().open();
        ObjectContext context = testContext.getServerRuntime().newContext();

        Language language = context.newObject(Language.class);
        language.setName("Asian");
        language.setIsActive((byte) 1);
        language.setIshVersion(333L);

        College college = context.newObject(College.class);
        college.setName("College");
        college.setFirstRemoteAuthentication(new Date());
        college.setTimeZone("Australia/Sydney");
        college.setRequiresAvetmiss(false);

        Course course = context.newObject(Course.class);
        course.setName("Course");
        course.setCollege(college);
        course.setEnrolmentType(CourseEnrolmentType.OPEN_FOR_ENROLMENT);

        createCourseClass(context, college, course, "1");
        createCourseClass(context, college, course, "2");
        createCourseClass(context, college, course, "3");
        createCourseClass(context, college, course, "4");
        createCourseClass(context, college, course, "5");

        context.commitChanges();
    }

    @Test
    public void testEquals() {
        List<CourseClass> srcList = getByObjectSelect(newContext());
        List<CourseClass> items = getByObjectSelect(newContext());

        srcList.removeAll(items);
        assertEquals(0, srcList.size());

        srcList = getByObjectSelect(newContext());

        Iterator<CourseClass> iterator = srcList.iterator();
        while(iterator.hasNext()) {
            if (items.contains(iterator.next())) {
                iterator.remove();
            }
        }
        assertEquals(0, srcList.size());

        srcList = getByRelation(newContext());
        srcList.removeAll(items);
        assertEquals(0, srcList.size());
    }

    @Test
    public void testHashCode() {
        Set<CourseClass> srcSet = new HashSet<>(getByObjectSelect(newContext()));
        List<CourseClass> items = getByObjectSelect(newContext());

        srcSet.removeAll(items);
        assertEquals(0, srcSet.size());

        srcSet = new HashSet<>(getByObjectSelect(newContext()));
        Iterator<CourseClass> iterator = srcSet.iterator();
        while(iterator.hasNext()) {
            if (items.contains(iterator.next())) {
                iterator.remove();
            }
        }
        assertEquals(0, srcSet.size());

        srcSet = new HashSet<>(getByRelation(newContext()));
        srcSet.removeAll(items);
        assertEquals(0, srcSet.size());
    }

    private void createCourseClass(ObjectContext context, College college, Course course, String courseClassCode) {
        CourseClass courseClass = context.newObject(CourseClass.class);
        courseClass.setCode(courseClassCode);
        courseClass.setCollege(college);
        courseClass.setCourse(course);
        courseClass.setCancelled(false);
        courseClass.setIsActive(true);
        courseClass.setIsDistantLearningCourse(false);
        courseClass.setMaximumPlaces(100);
    }

    private List<CourseClass> getByRelation(ObjectContext context) {
        Course course = ObjectSelect.query(Course.class).selectOne(context);
        return course.getCourseClasses();
    }

    private List<CourseClass> getByObjectSelect(ObjectContext context) {
        return ObjectSelect.query(CourseClass.class).select(context);
    }

    private ObjectContext newContext() {
        return testContext.getServerRuntime().newContext();
    }
}
