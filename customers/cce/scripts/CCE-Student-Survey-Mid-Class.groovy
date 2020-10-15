def run(arg) {
	def today = Calendar.getInstance().getTime()
		today.set(hourOfDay: 0, minute: 0, second: 0)

	def currentlyRunning = ObjectSelect.query(CourseClass)
				.where(CourseClass.END_DATE_TIME.lte(today))
				.and(CourseClass.START_DATE_TIME.gte(today))
				.and(CourseClass.IS_CANCELLED.eq(false))
				.select(args.context)


	currentlyRunning.each { cc ->

		midpoint = new Date((cc.startDateTime.getTime() - cc.endDateTime.getTime()) / 2 )
		midpoint.set(hourOfDay: 0, minute: 0, second: 0)
		
		if (midpoint == today) {
			if (!cc.hasTag("no survey")) {
				cc.successAndQueuedEnrolments.each { e ->
					email {
						to e.student.contact
						template "CCE Student Survey Mid Class"
						bindings enrolment: e
					}
				}
			}
		}
	}
}