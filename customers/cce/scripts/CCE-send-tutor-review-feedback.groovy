def run(args) {
	def today = new Date()
	today.set(hourOfDay: 0, minute: 0, second:0 )

	def classes = ObjectSelect.query(CourseClass)
				.where(CourseClass.IS_CANCELLED.eq(false))
				.and(CourseClass.END_DATE_TIME.eq(today-7))
				.select(args.context)


	classes.each { cc ->
		
		def surveys =  []
		cc.successAndQueuedEnrolments.each { e ->
			surveys.addAll(e.surveys) 
		} 
		

		if (surveys.size() > 0) {
			cc.tutorRoles.each { tr ->
				email {
					to tr.tutor.contact
					cc "natalia.borisova@sydney.edu.au", "jennifer.schmitzer@sydney.edu.au", "sarah.jessup@sydney.edu.au"
					template "CCE review student feedback"
					bindings contact: tr.tutor.contact, courseClass: cc
				}
			}
		}
	}
}		
