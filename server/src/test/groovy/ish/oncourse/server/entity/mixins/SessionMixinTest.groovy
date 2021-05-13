package ish.oncourse.server.entity.mixins


import ish.TestWithBootique
import ish.oncourse.server.cayenne.Session
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

import java.time.format.DateTimeFormatter

class SessionMixinTest {

    private static final String PATTERN = 'h:mm a EEEE d MMMM yyyy'
    private static final String SYDNEY_TIME = '6:55 AM Friday 12 June 2015'
    //minus 14 hours
    private static final String NEW_YORK_TIME = '4:55 PM Thursday 11 June 2015'


    private TimeZone timeZoneSydney = TimeZone.getTimeZone("Australia/Sydney")
    private TimeZone timeZoneNewYork = TimeZone.getTimeZone("America/New_York")

    
    @Test
    void testStart() {
        Calendar calendar = Calendar.getInstance(timeZoneSydney)
        calendar.set(year: 2015, month: 5, date: 12, hourOfDay: 6, minute: 55, second: 22)
        Date testDate = Date.from(calendar.toInstant())


        Session session1 = Mockito.mock(Session)
        Mockito.when(session1.startDatetime).thenReturn(testDate)
        Mockito.when(session1.timeZone).thenReturn(timeZoneSydney)

        Assertions.assertEquals(SYDNEY_TIME, session1.startDatetime.format(PATTERN, session1.timeZone))
        Assertions.assertEquals(SYDNEY_TIME, session1.start.format(DateTimeFormatter.ofPattern(PATTERN)))
        Assertions.assertEquals(session1.startDatetime.format(PATTERN, session1.timeZone), session1.start.format(DateTimeFormatter.ofPattern(PATTERN)))


        Session session2 = Mockito.mock(Session)
        Mockito.when(session2.startDatetime).thenReturn(testDate)
        Mockito.when(session2.timeZone).thenReturn(timeZoneNewYork)

        Assertions.assertEquals(NEW_YORK_TIME, session2.startDatetime.format(PATTERN, session2.timeZone))
        Assertions.assertEquals(NEW_YORK_TIME, session2.start.format(DateTimeFormatter.ofPattern(PATTERN)))
        Assertions.assertEquals(session2.startDatetime.format(PATTERN, session2.timeZone), session2.start.format(DateTimeFormatter.ofPattern(PATTERN)))
    }

    
    @Test
    void testEnd() {
        Calendar calendar = Calendar.getInstance(timeZoneSydney)
        calendar.set(year: 2015, month: 5, date: 12, hourOfDay: 6, minute: 55, second: 22)
        Date testDate = Date.from(calendar.toInstant())

        Session session1 = Mockito.mock(Session)
        Mockito.when(session1.endDatetime).thenReturn(testDate)
        Mockito.when(session1.timeZone).thenReturn(timeZoneSydney)

        Assertions.assertEquals(SYDNEY_TIME, session1.endDatetime.format(PATTERN, session1.timeZone))
        Assertions.assertEquals(SYDNEY_TIME, session1.end.format(DateTimeFormatter.ofPattern(PATTERN)))
        Assertions.assertEquals(session1.endDatetime.format(PATTERN, session1.timeZone), session1.end.format(DateTimeFormatter.ofPattern(PATTERN)))


        Session session2 = Mockito.mock(Session)
        Mockito.when(session2.endDatetime).thenReturn(testDate)
        Mockito.when(session2.timeZone).thenReturn(timeZoneNewYork)

        Assertions.assertEquals(NEW_YORK_TIME, session2.endDatetime.format(PATTERN, session2.timeZone))
        Assertions.assertEquals(NEW_YORK_TIME, session2.end.format(DateTimeFormatter.ofPattern(PATTERN)))
        Assertions.assertEquals(session2.endDatetime.format(PATTERN, session2.timeZone), session2.end.format(DateTimeFormatter.ofPattern(PATTERN)))
    }
}
