package ish.oncourse.solr.functions.course

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import org.apache.commons.lang3.time.DateUtils
import org.junit.Test
import org.mockito.Mockito

import static org.junit.Assert.assertEquals

class GetCourseStartDateTest {
    private GetCourseTestData data = new GetCourseTestData().with {
        it.current = new Date()
        it
    }

    @Test
    void test_class_is_distantLearning() {
        assertData(data.class_is_distantLearning, DateUtils.addDays(data.current, 1))
    }

    @Test
    void test_class_is_distantLearning_but_has_actual_sessions() {
        CourseClass courseClass = data.class_is_distantLearning_but_has_actual_sessions
        assertData(courseClass, courseClass.startDate)
    }

    @Test
    void test_class_does_not_have_any_sessions() {
        CourseClass courseClass = data.class_does_not_have_any_sessions
        assertData(courseClass, DateUtils.addYears(data.current, 100))
    }

    @Test
    void test_class_has_all_sessions_in_the_past() {
        CourseClass courseClass = data.class_has_all_sessions_in_the_past
        assertData(courseClass, DateUtils.addYears(data.current, 100))
    }


    @Test
    void test_class_has_an_actual_session_and_a_session_in_the_past() {
        CourseClass courseClass = data.class_has_an_actual_session_and_a_session_in_the_past
        assertData(courseClass, courseClass.startDate)
    }

    @Test
    void test_all_these_classes() {
        Course course = Mockito.mock(Course)

        CourseClass expectedClass = data.class_has_an_actual_session_and_a_session_in_the_past

        List<CourseClass> classes = [
                data.class_is_distantLearning,
                data.class_is_distantLearning_but_has_actual_sessions,
                data.class_does_not_have_any_sessions,
                data.class_has_all_sessions_in_the_past,
                expectedClass
        ]
        classes = classes.sort { CourseClass c1, CourseClass c2 ->
            c1.startDate <=> c2.startDate
        }
        assertEquals(new SCourseFunctions().with { it.current = data.current; it }.get(course, { classes }),
                expectedClass.startDate)
    }


    private assertData(CourseClass courseClass, Date expected) {
        Course course = Mockito.mock(Course)
//        assertEquals(new GetCourseStartDate().with { it.current = data.current; it }
//                .get(course, { [courseClass] }), expected)
    }

}
