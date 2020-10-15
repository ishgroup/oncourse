def run(args) {

	def includedTags = [ "Subjects/Leisure", "Subjects/SchoolAge", "Subjects/disAbility", "Subjects/English/Oral Communication", "Subjects/English/Written Communication", "Subjects/WorkSkills/Computers & Technology" ] 

	//get classes starting tomorrow
	def tomorrowStart = new Date() + 2
	tomorrowStart.set(hourOfDay: 0, minute: 0, second: 1) 

	def classesStartingTomorrow = ObjectSelect.query(CourseClass)
	          .where(CourseClass.START_DATE_TIME.between(tomorrowStart, tomorrowStart+1))
	          .and(CourseClass.IS_CANCELLED.eq(false))
	          .select(args.context) 

	def enrolmentsTomorrow = classesStartingTomorrow*.successAndQueuedEnrolments.flatten().findAll { e -> 
	// returns true is is a course has any tag or child tag of includedTags members
	e.student && e.student.contact.email && includedTags.find { t -> e.courseClass.course.hasTag(t, true) }
	}
	  
	enrolmentsTomorrow.each { e ->
	  email {
	    // to e.student.contact
	    from preference.email.from 
	    to "dhurley@sgscc.edu.au"
	    template "Student notice of class commencement"
	    bidings enrolment: e
	  }
	}

}