def checkTag(List<Tag> tags, String name) {
	tags.each { t ->
		if (t.name.equals(name)) {
			return true
		}
	}
	return false
}

def checkParent(List<Tag> tags, String name) {
	tags.each { t ->
		if (t.parentTag.name.equals(name)) {
			return true
		}
	}
	return false
}

def getLanguage(List<Tag> tags, String name) {
	tag.each { t ->
		if (t.parentTag.name.equals(name)) {
			return t
		}
	}
}

def run(args) {

	/**
	* Get classes ended yesterday to send feedback form
	*/
	def endDate = Calendar.getInstance().getTime()
    endDate.set(hourOfDay: 0, minute: 0, second: 0)

    def classes = ObjectSelect.query(CourseClass)
            .where(CourseClass.IS_CANCELLED.eq(false))
            .and(CourseClass.END_DATE_TIME.between(endDate - 2, endDate))
            .select(args.context)

    classes.each { cc ->

    	if ( checkTag(cc.tags, "Weekend Workshops") || checkTag(cc.tags, "Intensive Courses") ) {

    		cc.successAndQueuedEnrolments.each() { e ->
    			email {
    				template "WEA Syd Course Completion 2.0"
    				from "info@weasydney.nsw.edu.au"
    				to e.student.contact
    				bindings enrolment:e, option:1
    			}
    		}

    	} else if ( checkParent(cc.tags, "Languages") && !( checkTag(cc.tags, "Weekend Workshops") || checkTag(cc.tags, "Intensive Courses")) )  {

    		String language = getLanguage(cc.tags, "Languages").name

    		cc.successAndQueuedEnrolments.each() { e ->
    			email {
    				template "WEA Syd Course Completion 2.0"
    				from "info@weasydney.nsw.edu.au"
    				to e.student.contact
    				bindings enrolment:e, option:2, tag:language.replace(" ", "+")
    			}
    		}

    	} else {
    		def cclist = cc?.firstSession?.tutors?.first().courseClasses
    		cclist = cclist.unique().sort{ it.startDateTime }.findAll {it.startDateTime > endDate}

    		def list
    		if(cclist.size() >= 3){
    			cclist = cclist.subList(0, 3)
    		}

    		cc.successAndQueuedEnrolments.each() { e ->
    			email {
    				template "WEA Syd Course Completion 2.0"
    				from "info@weasydney.nsw.edu.au"
    				to e.student.contact
    				bindings enrolment:e, option:3, cclist:cclist
    			}
    		}


    	}

    }
}

	/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/**
	* 	Case 1
	* 	Class is tagged with 'Languages/Intensive Courses' and 'Languages/Weekend Workshops'
	*/
	/**
	*	Message
	*	To continue expanding your language skills, please visit our website to find a class suitable in Language Subject Name.
	*	(Links to the course name i.e. /courses/languages/subjectname)
	*/



	/**
	* 	Case 2
	* 	LANGUAGE CLASSES (LG WEEKEND WORKSHOP and INTENSIVES EXCLUDED Languages/Intensive Courses and Languages/Weekend Workshops)
	*/
	/**
	*	Message
	*	Your continuation class for the new term is available to enrol into online, please visit our website to enrol into Course Name.
	*	(Links to the course name i.e. /course/coursename)
	*	Next class in the new term, earch by Tutor’s upcoming classes and Subject Tag of finished class and correlate with same day (not date) and time as the finished class
	*/



	/**
	*	Case 3
	*	Excludes all language classes
	*/
	/**
	*	Message
	*	You might be interested in the following courses due to start soon:
	*	Course Name (Tutor of Class of Course Name) – StartDateTime
	*/
