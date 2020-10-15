def run(arg) {
	def today = Calendar.getInstance().getTime()
		today.set(hourOfDay: 0, minute: 0, second: 0)

	def endedYesterday = ObjectSelect.query(CourseClass)
				.and(CourseClass.END_DATE_TIME.between(today-1,today))
				.select(args.context)


	endedYesterday.each { cc ->		
		if (!cc.hasTag("no survey")) {
			cc.courseClassTutor.each { cct ->
				email {
					to cct.tutor.contact
					cc "natalia.borisova@sydney.edu.au", "jennifer.schmitzer@sydney.edu.au", "sarah.jessup@sydney.edu.au"
					from "info@cce.sydney.edu.au"
					subject "Course Evaluations Available"
					content "Dear ${cct.tutor.contact.fullName}, \n\n Evaluations have been received from your recent classes. To review them... \n\n Kind Regards, \n\n Natalia Borisova"
				}
			}
		}
	}
}