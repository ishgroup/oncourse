package ish.oncourse.portal.services.attendance

class Attendance {
	Long id
	Long studentId
	Integer type
	Integer durationMinutes
	String note
	Date sessionStart
	Date sessionEnd
	Date attendedFrom
	Date attendedUntil
}