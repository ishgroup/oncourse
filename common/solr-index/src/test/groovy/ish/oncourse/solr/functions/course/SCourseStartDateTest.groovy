package ish.oncourse.solr.functions.course

import ish.oncourse.model.CourseClass
import ish.oncourse.solr.model.SCourse
import org.apache.commons.lang3.time.DateUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static ish.oncourse.solr.functions.course.CourseTestFunctions.emptyCourseClassContext
import static ish.oncourse.solr.functions.course.CourseTestFunctions.resultIterator
import static org.junit.Assert.assertEquals

@RunWith(Parameterized.class)
class SCourseStartDateTest {

    @Parameterized.Parameter
    public Param param


    @Parameterized.Parameters
    static List<Param> data() {
        GetCourseTestData data = new GetCourseTestData()
        return [
                new Param(contexts: [data.class_is_distantLearning], expected: DateUtils.addDays(data.current, 1), data: data),
                new Param(contexts: [data.class_is_distantLearning_but_has_actual_sessions], expected: data.class_is_distantLearning_but_has_actual_sessions.courseClass.startDate, data: data),
                new Param(contexts: [data.class_does_not_have_any_sessions], expected: DateUtils.addYears(data.current, 100), data: data),
                new Param(contexts: [data.class_has_all_sessions_in_the_past], expected: DateUtils.addYears(data.current, 100), data: data),
                new Param(contexts: [data.class_has_an_actual_session_and_a_session_in_the_past], expected: data.class_has_an_actual_session_and_a_session_in_the_past.courseClass.sessions[0].startDate, data: data),
                new Param(contexts: [data.class_is_distantLearning,
                                     data.class_is_distantLearning_but_has_actual_sessions,
                                     data.class_does_not_have_any_sessions,
                                     data.class_has_all_sessions_in_the_past,
                                     data.class_has_an_actual_session_and_a_session_in_the_past
                ], expected: data.class_has_an_actual_session_and_a_session_in_the_past.courseClass.sessions[0].startDate, data: data)
        ]
    }


    @Test
    void test() {
        SCourse sCourse = SCourseFunctions.GetSCourse(getCourseContext(param))
        assertEquals(param.expected, sCourse.startDate)
    }


    private static CourseContext getCourseContext(Param param) {
        CourseContext context = CourseTestFunctions.emptyCourseContext()
        context.current = param.data.current
        context.course = param.data.course
        context.courseClasses = { resultIterator(param.contexts.collect { it.courseClass }) }
        context.applyCourseClass = SCourseFunctions.ApplyCourseClass
        context.courseClassContext = { CourseClass c, Date current ->
            CourseClassContext ccc = emptyCourseClassContext()
            ccc.courseClass = c
            ccc.sessions = { resultIterator(c.sessions) }
            ccc.current = current
            return ccc
        }
        context
    }


    static class Param {
        GetCourseTestData data
        List<CourseClassContext> contexts
        Date expected
    }
}
