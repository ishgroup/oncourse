package ish.oncourse.model;

import ish.oncourse.test.ContextUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.validation.ValidationException;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DiscountCourseClassTest {

    /**
     * Entity to test.
     */
    private DiscountCourseClass discountCourseClass;

    private ObjectContext context;
    private College college;
    private Discount discount;
    private CourseClass courseClass;
    private Course course;

    @Before
    public void setup() throws Exception {
        ContextUtils.setupDataSources();
        context = ContextUtils.createObjectContext();

        /**
         * Instance of any College entity.
         */
        college = context.newObject(College.class);
        college.setTimeZone("Australia/Sydney");
        college.setFirstRemoteAuthentication(new Date());
        college.setRequiresAvetmiss(false);

        context.commitChanges();
        /**
         * Instance of any Discount entity.
         */
        discount = context.newObject(Discount.class);
        discount.setCollege(college);
        discount.setHideOnWeb(false);
        context.commitChanges();
        /**
         * Instance of any Course entity.
         */
        course = context.newObject(Course.class);
        course.setCollege(college);

        context.commitChanges();
        /**
         * Instance of any CourseClass entity.
         */
        courseClass = context.newObject(CourseClass.class);
        courseClass.setMaximumPlaces(10);
        courseClass.setIsDistantLearningCourse(false);
        courseClass.setCollege(college);
        courseClass.setCourse(course);

        context.commitChanges();
    }

    @Test
    public void testRequiredCollegeId() throws Exception {
        discountCourseClass = context.newObject(DiscountCourseClass.class);
        assertNull("College status should be null before test", discountCourseClass.getCollege());

        /**
         *  Discount and CourseClass is required too
         */
        discountCourseClass.setDiscount(discount);
        discountCourseClass.setCourseClass(courseClass);

        boolean invalid = false;
        try {
            context.commitChanges();
        } catch (ValidationException ve) {
            invalid = true;
        }
        assertTrue("commit should be in failure status", invalid);
    }
}
