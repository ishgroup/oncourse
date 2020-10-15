/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/**
	Collects all classes that have the 'Not Confirmed' status anc checks if they meet two conditions to run
		1.Meets minimum enrolments (set in onCourse application)
		2.Makes over the total gross profit floor (set below)

	Confirmed classes recieve 'Course Information' email three days before class start
*/



def run (args) {

	/**
	 	Amount the class has to make to run
	 */
	def grossProfitThreshold = 500


	def today = Calendar.getInstance().getTime()
    today.set(hourOfDay: 0, minute: 0, second: 0)

    def classes = ObjectSelect.query(CourseClass)
            .where(CourseClass.IS_CANCELLED.eq(false))
            .and(CourseClass.START_DATE_TIME.gte(today))
            .select(args.context)

	classes = classes.findAll { cc -> cc.hasTag("Programs/Not Confirmed") && cc.isActive == true }

	classes.each { cc ->
		if ( cc.getSuccessAndQueuedEnrolments().size() >= cc.getMinimumPlaces() && cc.actualTotalProfit >= grossProfitThreshold ) {
			cc.removeTag("Programs/Not Confirmed")
			cc.addTag("Programs/Confirmed")
		}
	}
	args.context.commitChanges()
}
