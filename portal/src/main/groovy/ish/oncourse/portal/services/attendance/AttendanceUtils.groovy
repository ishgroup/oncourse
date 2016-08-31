package ish.oncourse.portal.services.attendance

import ish.common.types.AttendanceType
import ish.oncourse.model.Enrolment
import ish.oncourse.model.Attendance
import ish.oncourse.model.Session
import ish.oncourse.util.FormatUtils
import org.apache.cayenne.query.ObjectSelect

class AttendanceUtils {

	static Integer getAttendancePercent(Enrolment enrolment) {
		getAttendancePercent(getAttendances(enrolment))
	}

	static Integer getAttendancePercent(List<Attendance> attendances) {
		double minutesPassed = 0d;
		double minutesPresent = 0d;

		Date now = new Date()

		attendances.each { a ->
			if (a.session.endDate.before(now)) {
				if (a.attendanceType  && !AttendanceType.UNMARKED.databaseValue.equals(a.attendanceType)) {
					double sessionDuration = (a.session.endDate.time - a.session.startDate.time)/60000
					minutesPassed += sessionDuration
					if (AttendanceType.ATTENDED.databaseValue.equals(a.attendanceType) || AttendanceType.DID_NOT_ATTEND_WITH_REASON.databaseValue.equals(a.attendanceType)) {
						minutesPresent += sessionDuration;
					} else if (AttendanceType.PARTIAL.databaseValue.equals(a.attendanceType)) {
						Integer partialDuration = a.durationMinutes;
						if (partialDuration) {
							minutesPresent += Math.min(sessionDuration, partialDuration.doubleValue());
						}
					}
				}
			}
		}

		(int) (100 * minutesPresent / minutesPassed);
	}
	

	static List<Attendance> getAttendances(Enrolment enrolment) {
		 ObjectSelect.query(Attendance)
				.where(Attendance.STUDENT.eq(enrolment.student))
				.and(Attendance.SESSION.dot(Session.COURSE_CLASS).eq(enrolment.courseClass)).select(enrolment.objectContext);
	}

	static String getSessionTime(TimeZone timeZone, Session session){
		return String.format("%s - %s",
				FormatUtils.getDateFormat("h:mma", timeZone).format(session.getStartDate()),
				FormatUtils.getDateFormat("h:mma", timeZone).format(session.getEndDate()));
	}


	static String getStartDate(TimeZone timeZone, Session session){
		return getStartDate(timeZone, session, "EEE dd MMM")
	}

	static String getStartDate(TimeZone timeZone, Session session, String format){
		return  FormatUtils.getDateFormat(format, timeZone).format(session.getStartDate());
	}

	static String getSessionDateTime(TimeZone timeZone, Session session){
		return String.format("%s %s", getStartDate(timeZone, session), getSessionTime(timeZone, session));
	}
}
