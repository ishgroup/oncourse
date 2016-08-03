package ish.oncourse.portal.services.attendance

import ish.oncourse.model.Session

import static ish.common.types.AttendanceType.UNMARKED
import static ish.oncourse.model.auto._Attendance.ATTENDANCE_TYPE

/**
 * User: akoiro
 * Date: 4/08/2016
 */
enum SessionStyle {
    empty,
    marked,
    future,
    unmarked

    public static SessionStyle valueOf(Session session) {
        if (session.endDate.after(new Date()))  {
            return future
        } else if (session.attendances.empty) {
            return empty
        } else if (ATTENDANCE_TYPE.eq(UNMARKED.getDatabaseValue()).filterObjects(session.getAttendances()).empty) {
            return marked
        } else {
            return unmarked
        }
    }
}
