package ish.oncourse.portal.services.attendance

import ish.oncourse.model.Session

import static ish.common.types.AttendanceType.UNMARKED
import static ish.oncourse.model.auto._Attendance.ATTENDANCE_TYPE
import static java.util.Calendar.DAY_OF_MONTH
import static org.apache.commons.lang3.time.DateUtils.addDays
import static org.apache.commons.lang3.time.DateUtils.truncate

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
        Date date = addDays(truncate(new Date(), DAY_OF_MONTH), 1);
        if (session.startDate.after(date))  {
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
