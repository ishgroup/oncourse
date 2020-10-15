/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def run(args) {

	def endDate =  new Date()
	endDate.set(hourOfDay: 0, minute: 0, second: 0)

	def allClasses = ObjectSelect.query(CourseClass)
            .where(CourseClass.IS_CANCELLED.eq(false))
            .and(CourseClass.END_DATE_TIME.between(endDate +7, endDate +8))
            .select(args.context)


    allClasses = allClasses.findAll { cc ->
        cc.course.hasTag("Subjects/Languages", true)
    }



    allClasses.each { cc ->
        cc.successAndQueuedEnrolments.each { e ->
    		email {
    			to e.student.contact
    			template	'SCC discount to language enrolment'
    			bindings	enrolment: e
    		}
    	}
    }
}
