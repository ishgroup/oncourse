package ish.oncourse.solr.functions.course

import ish.oncourse.model.Session
import org.apache.commons.lang3.time.DateUtils
import org.junit.Test

import java.time.LocalDate

import static ish.oncourse.solr.functions.course.SessionFunctions.*
import static java.sql.Date.valueOf
import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * User: akoiro
 * Date: 24/10/17
 */
class SessionFunctionsTest {

    @Test
    void test_getDayName() {

        Session session = mock(Session)
        when(session.startDate).thenReturn(valueOf(LocalDate.of(2017, 10, 23)))
        assertEquals("Monday", getDayName(session))
        when(session.startDate).thenReturn(valueOf(LocalDate.of(2017, 10, 24)))
        assertEquals("Tuesday", getDayName(session))
        when(session.startDate).thenReturn(valueOf(LocalDate.of(2017, 10, 25)))
        assertEquals("Wednesday", getDayName(session))
        when(session.startDate).thenReturn(valueOf(LocalDate.of(2017, 10, 26)))
        assertEquals("Thursday", getDayName(session))
        when(session.startDate).thenReturn(valueOf(LocalDate.of(2017, 10, 27)))
        assertEquals("Friday", getDayName(session))
        when(session.startDate).thenReturn(valueOf(LocalDate.of(2017, 10, 28)))
        assertEquals("Saturday", getDayName(session))
        when(session.startDate).thenReturn(valueOf(LocalDate.of(2017, 10, 29)))
        assertEquals("Sunday", getDayName(session))
    }

    @Test
    void test_getDayType() {
        Session session = mock(Session)
        when(session.startDate).thenReturn(valueOf(LocalDate.of(2017, 10, 23)))
        assertEquals("weekday", getDayType(session))
        when(session.startDate).thenReturn(valueOf(LocalDate.of(2017, 10, 24)))
        assertEquals("weekday", getDayType(session))
        when(session.startDate).thenReturn(valueOf(LocalDate.of(2017, 10, 25)))
        assertEquals("weekday", getDayType(session))
        when(session.startDate).thenReturn(valueOf(LocalDate.of(2017, 10, 26)))
        assertEquals("weekday", getDayType(session))
        when(session.startDate).thenReturn(valueOf(LocalDate.of(2017, 10, 27)))
        assertEquals("weekday", getDayType(session))
        when(session.startDate).thenReturn(valueOf(LocalDate.of(2017, 10, 28)))
        assertEquals("weekend", getDayType(session))
        when(session.startDate).thenReturn(valueOf(LocalDate.of(2017, 10, 29)))
        assertEquals("weekend", getDayType(session))
    }

    @Test
    void test_getDayTime() {
        Session session = mock(Session)
        use(DateUtils) {
            when(session.startDate).thenReturn(new Date().truncate(Calendar.DAY_OF_MONTH).addHours(7).addMinutes(1))
            assertEquals("daytime", getDayTime(session))
            when(session.startDate).thenReturn(new Date().truncate(Calendar.DAY_OF_MONTH).addHours(16).addMinutes(1))
            assertEquals("daytime", getDayTime(session))

            when(session.startDate).thenReturn(new Date().truncate(Calendar.DAY_OF_MONTH).addHours(17).addMinutes(1))
            assertEquals("evening", getDayTime(session))
            when(session.startDate).thenReturn(new Date().truncate(Calendar.DAY_OF_MONTH).addHours(6).addMinutes(1))
            assertEquals("evening", getDayTime(session))
        }
    }

}
