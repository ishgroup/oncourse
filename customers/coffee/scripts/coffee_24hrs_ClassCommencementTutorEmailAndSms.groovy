/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def run(args) {
	def tomorrowStart = new Date() + 1
	tomorrowStart.set(hourOfDay: 0, minute: 0, second: 0)

	def tomorrowEnd = new Date() + 3
	tomorrowEnd.set(hourOfDay: 0, minute: 0, second: 0)

	def context = args.context

	def exp = CourseClass.IS_CANCELLED.eq(false)
			.andExp(CourseClass.START_DATE_TIME.ne(null))
			.andExp(CourseClass.START_DATE_TIME.between(tomorrowStart, tomorrowEnd))

	def classesStartingTomorrow = context.select(SelectQuery.query(CourseClass, exp))

	classesStartingTomorrow.each() { courseClass ->

		if (courseClass.successAndQueuedEnrolments.size() >= 0) {

			courseClass.tutorRoles.each() { role ->
				email {
					template "Tutor notice of class commencement"
					to role.tutor.contact
					bindings courseClass: courseClass, tutor: role.tutor
				}

				if (role?.tutor?.contact?.mobilePhone != null) {
					sms {
					to role.tutor.contact
					text "${courseClass.course.code} class starts ${courseClass.startDateTime.format("h:mm a d MMMM", courseClass.getTimeZone())} at ${courseClass.displayableLocation}".take(160)
					}
				}

				context.commitChanges()
			}
		}
	}
}
