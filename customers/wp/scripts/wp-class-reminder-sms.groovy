/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def run(args) {

	def todayStart = new Date()
	todayStart.set(hourOfDay: 0, minute: 0, second: 1)

	def SUNDAY = 0
	def FRIDAY = 5
	def SATURDAY = 6

	def classes
	def startingOn = "tomorrow"


	if (todayStart.day == SUNDAY || todayStart.day == SATURDAY) {
		return // don't send messages on a weekend
	}

	def classStart = todayStart+1

	if (todayStart.day == FRIDAY) {
		classStart += 2 // skip the weekend
		startingOn = "on Monday"
	}

	// if its friday, get all classes starting on the next Monday
	classes = ObjectSelect.query(CourseClass)
			.where(CourseClass.START_DATE_TIME.between(classStart, classStart+1))
			.and(CourseClass.IS_CANCELLED.eq(false))
			.select(args.context)


	classes.removeAll { cc -> cc.uniqueCode.startsWith("HR") || cc.uniqueCode.startsWith("SWRC") ||cc.uniqueCode.startsWith("EL") || cc.uniqueCode.startsWith("BLOCK") }

	classes.each { c ->
		c.successAndQueuedEnrolments.each { e ->
			if (e.student.contact.mobilePhone != null) {

				def msg = "Msg from PTS: reminder that your training starts ${startingOn} at ${e.courseClass.startDateTime.format("h:mm a", e.courseClass.timeZone)}. For more details or if you can't attend contact PTS on 94117888 (Ref:${e.courseClass.uniqueCode})".take(160)

				// email {
				// 	to "jacqui.jones@westernpower.com.au"
				// 	from preference.email.from
				// 	subject "Message for student " + e.student.contact.fullName + " for class " + e.courseClass.uniqueCode
				// 	content msg
				// }

				sms {
					to e.student.contact
					text msg
				}
			}
		}

	}
}
