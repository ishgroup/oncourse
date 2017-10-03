package ish.oncourse.solr.functions.course

import com.github.javafaker.Faker
import ish.math.Money
import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Session

import static ish.oncourse.solr.functions.course.ClassType.valueOf
import static ish.oncourse.solr.functions.course.ClassType.withOutSessions
import static org.apache.commons.lang3.time.DateUtils.addDays
import static org.apache.commons.lang3.time.DateUtils.addHours
import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Mock objects to test GetCourseStartDate implementation
 */
class GetCourseTestData {
    Faker faker = new Faker()
    Date current = new Date()
    Course course = mock(Course)

    GetCourseTestData() {
        when(course.code).thenReturn("COURSE")
    }

    CourseClass getClass_is_distantLearning() {
        CourseClass courseClass = mockCourseClass()
        when(courseClass.isDistantLearningCourse).thenReturn(true)
        courseClass
    }

    CourseClass getClass_is_distantLearning_but_has_actual_sessions() {
        CourseClass courseClass = mockCourseClass()
        when(courseClass.isDistantLearningCourse).thenReturn(true)
        List<Session> sessions = [
                mockSession(1, 2),
                mockSession(2, 3),
        ]
        when(courseClass.sessions).thenReturn(sessions)
        Date start = sessions[0].startDate
        when(courseClass.startDate).thenReturn(start)
        Date end = sessions[0].endDate
        when(courseClass.endDate).thenReturn(end)
        courseClass
    }

    CourseClass getClass_does_not_have_any_sessions() {
        //course class doesn't have any sessions
        CourseClass courseClass = mockCourseClass()
        when(courseClass.startDate).thenReturn(addDays(current, 1))
        courseClass
    }

    CourseClass getClass_has_all_sessions_in_the_past() {
        //course class has  sessions in the past
        CourseClass courseClass = mockClass(-48, -23)
        List<Session> sessions = [
                mockSession(-24, -23),
                mockSession(-48, -47),
        ]
        when(courseClass.sessions).thenReturn(sessions)
        assertEquals(withOutSessions, valueOf(courseClass))
        courseClass
    }

    CourseClass getClass_has_an_actual_session_and_a_session_in_the_past() {
        //course class has  sessions in the past
        CourseClass courseClass = mockClass(-5, 5)
        List<Session> sessions = [
                mockSession(-5, -1),
                mockSession(1, 5),
        ]
        when(courseClass.sessions).thenReturn(sessions)
        courseClass
    }

    CourseClass mockClass(int sHours, int eHours) {
        CourseClass courseClass = mockCourseClass()
        when(courseClass.startDate).thenReturn(addHours(current, sHours))
        when(courseClass.endDate).thenReturn(addHours(current, eHours))
        courseClass
    }

    private CourseClass mockCourseClass() {
        CourseClass courseClass = mock(CourseClass)
        when(courseClass.course).thenReturn(course)
        when(courseClass.feeExGst).thenReturn(new Money(999, 99))
        when(courseClass.code).thenReturn("CLASS")
        courseClass
    }


    Session mockSession(int sHours, int eHours) {
        Session session = mock(Session)
        when(session.startDate).thenReturn(addHours(current, sHours))
        when(session.endDate).thenReturn(addHours(current, eHours))
        session
    }


}
