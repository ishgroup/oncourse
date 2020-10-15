/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def run(arg) {
	def today = Calendar.getInstance().getTime()
		today.set(hourOfDay: 0, minute: 0, second: 0)

	def startsTomorrow = ObjectSelect.query(CourseClass)
				.and(CourseClass.START_DATE_TIME.between(today, today+1))
				.and(CourseClass.IS_CANCELLED.eq(false))
				.select(args.context)


	startsTomorrow.each { cc ->
		if (!cc.hasTag("no survey")) {
			cc.courseClassTutor.each { cct ->
				email {
					to cct.tutor.contact
					template "CCE Student Survey Mid Class"
					bindings courseClassTutor: cct
				}
			}
		}
	}
}
