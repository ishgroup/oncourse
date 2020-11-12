records.collectMany { CourseClass cc -> cc.enrolments }.collectMany { e -> e.outcomes }.each { o ->
	csv << [
			"studentNumber": o.enrolment.student.studentNumber,
			"firstName"    : o.enrolment.student.contact.firstName,
			"lastName"     : o.enrolment.student.contact.lastName,
			"classCode"    : "${o.enrolment.courseClass.course.code}-${o.enrolment.courseClass.code}",
			"nationalCode" : o.module?.nationalCode,
			"title"        : o.module?.title,
			"startDate"    : o.startDate?.format("d/M/Y hh:mm a"),
			"endDate"      : o.endDate?.format("d/M/Y hh:mm a"),
			"status"       : o.status?.displayName
	]
}
