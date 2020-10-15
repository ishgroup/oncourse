def run (args) {
	def today = new Date()
	today.set(hourOfDay: 0, minute: 0, second: 0)

	def classes = ObjectSelect.query(CourseClass)
	    .where(CourseClass.IS_CANCELLED.eq(false))
	    .and(CourseClass.START_DATE_TIME.between(today + 3, today + 4))
	    .select(args.context)

    classes.findAll { cc -> cc.hasTag("Programs/Confirmed") }   

    classes.each { cc ->
    	cc.successAndQueuedEnrolments.each { e ->
    		email {
    			template "CCE Course Reminder"
    			bindings enrolment: e
    			to e.student.contact
    		}
    	}
        
        cc?.tutorRoles.each { tr ->
            email {
                template "CCE Course Reminder Tutor"
                to tr.tutor.contact
                bindings contact : tr.tutor.contact, courseClass : cc
            }
        }
    }
}