/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

records.each { Enrolment e ->
	csv << [
			"courseCode"                        : e.courseClass.course.code,
			"courseName"                        : e.courseClass.course.name,
			"studentLastName"                   : e.student.contact.lastName,
			"studentFirstName"                  : e.student.contact.firstName,
			"studentMobilePhone"                : e.student.contact.mobilePhone,
			"studentEnrolmentDate"				: e.createdOn,
			"classEndDateTime"                  : e.courseClass.endDateTime?.format("yyyy-MM-dd'T'HH:mm:ssXXX", e.courseClass.timeZone),
			"studentEmail"                      : e.student.contact.email
	]
}
