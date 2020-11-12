	records.collectMany { cc -> cc.sessions }.collectMany { s -> s.attendance }.each { a ->
		csv << [
				"CourseName"         	: a.session.courseClass.course.name,
				"Course-ClassCode"      : a.session.courseClass.course.code + "-" + a.session.courseClass.code,
				"Student"				: a.student.contact.fullName,
				"Session Start Time"    : a.session.startDatetime?.format("d/M/Y hh:mm a"),
				"Attendance Type"       : a.attendanceType,
				"Attended From"			: a.attendedFrom?.format("d/M/Y hh:mm a"),
				"Attended Until"		: a.attendedUntil?.format("d/M/Y hh:mm a"),
				"Duration"				: a.durationMinutes,
				"Note"					: a.note,
				"Marked By Tutor"		: a.markedByTutor?.contact?.fullName ?: "Office",
				"Modified On"			: a.modifiedOn?.format("d/M/Y hh:mm a"),
		]
	}
