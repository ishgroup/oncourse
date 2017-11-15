package ish.oncourse.solr.functions.course

import ish.oncourse.model.Session
import org.junit.Test

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

import static ish.oncourse.solr.functions.course.SessionFunctions.*
import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * User: akoiro
 * Date: 24/10/17
 */
class SessionFunctionsTest {


    private static Date toDate(LocalDate localDate, ZoneId zoneId = ZoneId.systemDefault()) {
        return Date.from(localDate.atStartOfDay(zoneId).toInstant())
    }

    private static Date toDate(LocalDateTime localDate, ZoneId zoneId = ZoneId.systemDefault()) {
        return Date.from(localDate.atZone(zoneId).toInstant())
    }

    /**
     * format "2010-10-01 18:00:00"
     */
    private static Date toDate(String mysqlDate) {
        return new SimpleDateFormat('yyyy-MM-dd HH:mm:ss').parse(mysqlDate)
    }


    @Test
    void test_getDayTime_Zone() {
        Date date = toDate("2017-10-23  18:00:00")
        Session session = mock(Session)
        when(session.startDate).thenReturn(date)
        assertEquals("evening", getDayTime(session))

        when(session.timeZone).thenReturn("Australia/Perth")
        assertEquals("daytime", getDayTime(session))
    }


    @Test

    void test_getDayType_Zone() {
        Date date = toDate("2017-10-28 01:00:00")
        Session session = mock(Session)

        when(session.startDate).thenReturn(date)
        when(session.timeZone).thenReturn("Australia/Sydney")
        assertEquals("weekend", getDayType(session))

        when(session.timeZone).thenReturn("Australia/Perth")
        assertEquals("weekday", getDayType(session))
    }

    @Test
    void test_getDayName_Zone() {
        Date date = toDate("2017-10-28 01:00:00")
        Session session = mock(Session)
        when(session.startDate).thenReturn(date)
        when(session.timeZone).thenReturn("Australia/Sydney")
        assertEquals("Saturday", getDayName(session))

        when(session.timeZone).thenReturn("Australia/Perth")
        assertEquals("Friday", getDayName(session))
    }


    @Test
    void test_getDayName() {

        Session session = mock(Session)
        when(session.startDate).thenReturn(toDate("2017-10-23 18:00:00"))
        assertEquals("Monday", getDayName(session))
        when(session.startDate).thenReturn(toDate("2017-10-24 18:00:00"))
        assertEquals("Tuesday", getDayName(session))
        when(session.startDate).thenReturn(toDate("2017-10-25 18:00:00"))
        assertEquals("Wednesday", getDayName(session))
        when(session.startDate).thenReturn(toDate("2017-10-26 18:00:00"))
        assertEquals("Thursday", getDayName(session))
        when(session.startDate).thenReturn(toDate("2017-10-27 18:00:00"))
        assertEquals("Friday", getDayName(session))
        when(session.startDate).thenReturn(toDate("2017-10-28 18:00:00"))
        assertEquals("Saturday", getDayName(session))
        when(session.startDate).thenReturn(toDate("2017-10-29 18:00:00"))
        assertEquals("Sunday", getDayName(session))
    }

    @Test
    void test_getDayType() {
        Session session = mock(Session)
        when(session.startDate).thenReturn(toDate("2017-10-23 18:00:00"))
        assertEquals("weekday", getDayType(session))
        when(session.startDate).thenReturn(toDate("2017-10-24 18:00:00"))
        assertEquals("weekday", getDayType(session))
        when(session.startDate).thenReturn(toDate("2017-10-25 18:00:00"))
        assertEquals("weekday", getDayType(session))
        when(session.startDate).thenReturn(toDate("2017-10-26 18:00:00"))
        assertEquals("weekday", getDayType(session))
        when(session.startDate).thenReturn(toDate("2017-10-27 18:00:00"))
        assertEquals("weekday", getDayType(session))
        when(session.startDate).thenReturn(toDate("2017-10-28 18:00:00"))
        assertEquals("weekend", getDayType(session))
        when(session.startDate).thenReturn(toDate("2017-10-29 18:00:00"))
        assertEquals("weekend", getDayType(session))
    }

    @Test
    void test_getDayTime() {
        Session session = mock(Session)
        when(session.startDate).thenReturn(toDate("2017-10-29 07:01:00"))
        assertEquals("daytime", getDayTime(session))
        when(session.startDate).thenReturn(toDate("2017-10-29 16:01:00"))
        assertEquals("daytime", getDayTime(session))

        when(session.startDate).thenReturn(toDate("2017-10-29 17:01:00"))
        assertEquals("evening", getDayTime(session))
        when(session.startDate).thenReturn(toDate("2017-10-29 06:01:00"))
        assertEquals("evening", getDayTime(session))
    }

}
