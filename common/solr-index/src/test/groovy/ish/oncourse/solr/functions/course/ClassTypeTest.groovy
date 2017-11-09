package ish.oncourse.solr.functions.course

import org.junit.Test

import static ish.oncourse.solr.functions.course.ClassType.*
import static org.junit.Assert.assertEquals

/**
 * User: akoiro
 * Date: 9/11/17
 */
class ClassTypeTest {
    private GetCourseTestData data = new GetCourseTestData()

    @Test
    void test_class_is_distantLearning() {
        assertEquals(distantLearning, valueOf(data.class_is_distantLearning))
    }

    @Test
    void test_class_is_distantLearning_but_has_actual_sessions() {
        assertEquals(regular, valueOf(data.class_is_distantLearning_but_has_actual_sessions))
    }

    @Test
    void test_class_does_not_have_any_sessions() {
        assertEquals(withOutSessions, valueOf(data.class_does_not_have_any_sessions))
    }

    @Test
    void test_class_has_all_sessions_in_the_past() {
        assertEquals(withOutSessions, valueOf(data.class_has_all_sessions_in_the_past))
    }

    @Test
    void test_class_has_an_actual_session_and_a_session_in_the_past() {
        assertEquals(regular, valueOf(data.class_has_an_actual_session_and_a_session_in_the_past))
    }

}
