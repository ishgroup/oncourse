/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

records.sort{ it.session.startDatetime }.each { Attendance a ->
	csv << [
			"Date" : a.session.startDatetime?.format("d/M/Y", a.session.timeZone),
			"Start time" : a.session.startDatetime?.format("hh:mm a", a.session.timeZone),
			"End time" : a.session.endDatetime?.format("hh:mm a", a.session.timeZone),
			"Student" : a.student.fullName,
			"Location" : a.session.room?.site?.name,
			"Class code" : a.session.courseClass.course.code + "-" + a.session.courseClass.code,
			"Course name" : a.session.course.name
	]
}
