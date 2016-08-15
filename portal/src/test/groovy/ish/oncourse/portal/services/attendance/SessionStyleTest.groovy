package ish.oncourse.portal.services.attendance

import ish.common.types.AttendanceType
import ish.oncourse.model.Session
import org.apache.commons.lang3.time.DateUtils
import org.junit.Test

import static ish.common.types.AttendanceType.UNMARKED
import static ish.oncourse.portal.services.attendance.SessionStyle.valueOf
import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * User: akoiro
 * Date: 15/08/2016
 */
class SessionStyleTest {

    @Test
    public void testMarked() {
        Session session = mock(Session);
        when(session.startDate).thenReturn(new Date())
        ish.oncourse.model.Attendance attendance = mock(ish.oncourse.model.Attendance)
        when(attendance.attendanceType).thenReturn(AttendanceType.ATTENDED.databaseValue)
        when(session.attendances).thenReturn(Collections.singletonList(attendance))

        assertEquals(SessionStyle.marked, valueOf(session));
    }

    @Test
    public void testEmpty() {
        Session session = mock(Session);
        when(session.startDate).thenReturn(new Date())
        assertEquals(SessionStyle.empty, valueOf(session));
    }

    @Test
    public void testUnmarked() {
        Session session = mock(Session);
        when(session.startDate).thenReturn(new Date())
        ish.oncourse.model.Attendance attendance = mock(ish.oncourse.model.Attendance)
        when(attendance.readNestedProperty(ish.oncourse.model.Attendance.ATTENDANCE_TYPE.name)).thenReturn(UNMARKED.databaseValue)
        when(session.attendances).thenReturn(Collections.singletonList(attendance))
        assertEquals(SessionStyle.unmarked, valueOf(session));
    }

    @Test
    public void testFuture() {
        Session session = mock(Session);
        when(session.startDate).thenReturn(DateUtils.addDays(new Date(), 1))
        assertEquals(SessionStyle.future, valueOf(session));
        session
    }



}
