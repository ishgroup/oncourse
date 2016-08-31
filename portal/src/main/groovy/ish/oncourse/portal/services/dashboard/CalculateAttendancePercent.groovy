package ish.oncourse.portal.services.dashboard

import groovy.time.TimeCategory
import ish.oncourse.model.Attendance
import ish.oncourse.model.Session
import ish.oncourse.model.Student
import ish.oncourse.portal.services.attendance.AttendanceUtils
import org.apache.cayenne.query.ObjectSelect

class CalculateAttendancePercent {
	
	def Student student
	private List<Attendance> attendances
	
	def CalculateAttendancePercent(Student student) {
		this.student = student
	}
	
	def int calculate() {
		AttendanceUtils.getAttendancePercent(getAttendance())
	}

	
	def List<Attendance> getAttendance() {
		if (!student) {
			return Collections.EMPTY_LIST;
		} else if (attendances == null) {
			Date yearAgo
			Date now = new Date()

			use(TimeCategory) {
				yearAgo = now - 1.year
			}
			attendances = ObjectSelect.query(Attendance).where(Attendance.STUDENT.eq(student))
					.and(Attendance.SESSION.dot(Session.START_DATE).between(yearAgo, now)).select(student.getObjectContext())
		}
		return attendances;
	}
}
