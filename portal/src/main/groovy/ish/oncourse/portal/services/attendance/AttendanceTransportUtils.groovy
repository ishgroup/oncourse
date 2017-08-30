package ish.oncourse.portal.services.attendance

import ish.oncourse.model.Tutor
import org.apache.cayenne.Cayenne
import org.apache.cayenne.ObjectContext

import java.util.concurrent.TimeUnit

/**
 * Created by pavel on 8/22/17.
 */
class AttendanceTransportUtils {

    static final int MILLIS_IN_MINUTE = 60000;

    static List<Attendance>  toContainerAttendanceList(List<ish.oncourse.model.Attendance> attendaces) {
        List<Attendance> response = new ArrayList<>()
        for (ish.oncourse.model.Attendance attendance : attendaces) {
            response.add(toContainerAttendance(attendance))
        }
        return response;
    }

    /**
     * Convert Cayenne Attendance object to transport object, with timezone shift (during converting transport object to
     * JSON all dates lost information about timezones)
     * @param attendance cayenne attendance object
     * @return transport attendance object
     */
    static Attendance toContainerAttendance(ish.oncourse.model.Attendance attendance) {

        def shiftDateByTimezoneOffset = {date, timezone ->
            return date != null ? new Date((long)date.time + timezone.rawOffset - TimeZone.default.rawOffset) : null
        }

        TimeZone timezone = TimeZone.getTimeZone(attendance.session.timeZone)

        Attendance attendanceNode = new Attendance().with {
            it.id = attendance.id
            it.studentId = attendance.student.id
            it.type = attendance.attendanceType
            it.note = attendance.note
            it.sessionStart = shiftDateByTimezoneOffset.call(attendance.session.startDate, timezone)
            it.sessionEnd = shiftDateByTimezoneOffset.call(attendance.session.endDate, timezone)

            it.durationMinutes = attendance.durationMinutes

            // if duration time filled only need to set attendedFrom, attendedUntil
            if (attendance.durationMinutes != null && (it.attendedFrom == null || it.attendedUntil == null)) {
                it.attendedUntil = shiftDateByTimezoneOffset.call(attendance.session.endDate, timezone)
                it.attendedFrom = shiftDateByTimezoneOffset(new Date(attendance.session.endDate.time - attendance.durationMinutes * MILLIS_IN_MINUTE), timezone)
            } else {
                it.attendedFrom = shiftDateByTimezoneOffset.call(attendance.attendedFrom, timezone)
                it.attendedUntil = shiftDateByTimezoneOffset.call(attendance.attendedUntil, timezone)
            }

            return it
        }
        return attendanceNode
    }

    /**
     * Convert transport Attendance object to Cayenne Attendance
     * @param context
     * @param attendance
     * @param tutor
     * @return
     */
    static ish.oncourse.model.Attendance toDBOAttendance(ObjectContext context, Attendance attendance, Tutor markedBy) {
        ish.oncourse.model.Attendance att = Cayenne.objectForPK(context, ish.oncourse.model.Attendance.class, attendance.id)
        att.attendanceType = attendance.type
        att.note = attendance.note
        att.attendedFrom = attendance.attendedFrom
        att.attendedUntil = attendance.attendedUntil

        att.durationMinutes = TimeUnit.MILLISECONDS
                .toMinutes(attendance.attendedUntil.time - attendance.attendedFrom.time)

        att.markedByTutor = markedBy
        att.markedByTutorDate = new Date()
        return att
    }
}
