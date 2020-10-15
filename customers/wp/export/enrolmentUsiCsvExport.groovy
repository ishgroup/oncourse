records.each { Enrolment e ->
	csv << [
			"courseCode"                        : e.courseClass.course.code,
			"classCode"                         : e.courseClass.code,
			"courseName"                        : e.courseClass.course.name,
			"classStartDateTime"                : e.courseClass.startDateTime?.format("yyyy-MM-dd hh:mm"),
			"classEndDateTime"                  : e.courseClass.endDateTime?.format("yyyy-MM-dd hh:mm"), 
			"enrolmentStatus"                   : e.status.displayName,
			"studentLastName"                   : e.student.contact.lastName,
			"studentFirstName"                  : e.student.contact.firstName,
			"USI"								: e.student.usi,
			"USI Status"						: e.student.usiStatus,
			"studentBirthDate"                  : e.student.contact.birthDate?.format("yyyy-MM-dd"),
			"studentEmail"                      : e.student.contact.email
	]
}
