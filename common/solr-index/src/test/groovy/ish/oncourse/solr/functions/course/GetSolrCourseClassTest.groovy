package ish.oncourse.solr.functions.course

import ish.oncourse.solr.model.SCourseClass
import org.apache.commons.lang3.time.DateUtils
import org.junit.Test

import static org.junit.Assert.assertEquals

/**
 * User: akoiro
 * Date: 3/10/17
 */
class GetSolrCourseClassTest {
    private GetCourseTestData data = new GetCourseTestData().with {
        it.current = new Date()
        it
    }

    @Test
    void test_class_is_distantLearning() {
        SCourseClass courseClass = new GetSCourseClass(data.class_is_distantLearning, data.current).get()
        assertClass(new SCourseClass().with {
            classStart = DateUtils.addDays(data.current, 1)
            classEnd = DateUtils.addYears(data.current, 100)
            classCode = 'COURSE-CLASS'
            price = '999.99'
            it
        }, courseClass)
    }

    @Test
    void test_class_is_distantLearning_but_has_actual_sessions() {
        SCourseClass courseClass = new GetSCourseClass(data.class_is_distantLearning_but_has_actual_sessions, data.current).get()
        assertClass(new SCourseClass().with {
            classStart = data.class_is_distantLearning_but_has_actual_sessions.startDate
            classEnd = data.class_is_distantLearning_but_has_actual_sessions.endDate
            classCode = 'COURSE-CLASS'
            price = '999.99'
            it
        }, courseClass)
    }

    @Test
    void test_class_does_not_have_any_sessions() {
        SCourseClass cc = data.class_does_not_have_any_sessions
        SCourseClass scc = new GetSCourseClass(cc, data.current).get()
        assertClass(new SCourseClass().with {
            classStart = DateUtils.addYears(data.current, 100)
            classEnd = DateUtils.addYears(data.current, 100)
            classCode = 'COURSE-CLASS'
            price = '999.99'
            it
        }, scc)
    }

    @Test
    void test_class_has_all_sessions_in_the_past() {
        SCourseClass cc = data.class_has_all_sessions_in_the_past
        SCourseClass scc = new GetSCourseClass(cc, data.current).get()
        assertClass(new SCourseClass().with {
            classStart = DateUtils.addYears(data.current, 100)
            classEnd = DateUtils.addYears(data.current, 100)
            classCode = 'COURSE-CLASS'
            price = '999.99'
            it
        }, scc)
    }


    @Test
    void test_class_has_an_actual_session_and_a_session_in_the_past() {
        SCourseClass cc = data.class_has_an_actual_session_and_a_session_in_the_past
        SCourseClass scc = new GetSCourseClass(cc, data.current).get()
        assertClass(new SCourseClass().with {
            classStart = cc.startDate
            classEnd = cc.endDate
            classCode = 'COURSE-CLASS'
            price = '999.99'
            it
        }, scc)
    }

    private void assertClass(SCourseClass expected, SCourseClass actual) {
        assertEquals("classCode", expected.classCode, 'COURSE-CLASS')
        assertEquals("price", expected.price, '999.99')
        assertEquals("classStart", expected.classStart, actual.classStart)
        assertEquals("classEnd", expected.classEnd, actual.classEnd)
    }
}
