package ish.oncourse.portal.services.attendance

class Attendance {
	def Long id
	def Long studentId
	def Integer type
	def Integer durationMinutes
	def String note
	def Date startDate
	def Date endDate
	
	public static Attendance valueOf(ish.oncourse.model.Attendance attendance) {
		Attendance attendanceNode = new Attendance().with {
			it.id = attendance.id
			it.studentId = attendance.student.id
			it.type = attendance.attendanceType
			it.durationMinutes = attendance.durationMinutes
			it.note = attendance.note
			it.startDate = attendance.session.startDate
			it.endDate = attendance.session.endDate
			return it
		}
		return attendanceNode
	}
}