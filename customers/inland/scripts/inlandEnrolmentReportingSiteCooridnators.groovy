/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import groovy.time.TimeCategory
import ish.common.types.PaymentSource

def run(args) {

	def upperDate = new Date()
	upperDate.set(hourOfDay: 0, minute: 0, second: 0)

	def lowerDate = upperDate - 1


	def centreNames = ["Barraba", "Bingara", "Gunnedah", "Inverell", "Moree", "Narrabri", "Warialda"]

	def queryEnrolments = ObjectSelect.query(Enrolment)
    .where(Enrolment.CREATED_ON.between(lowerDate, upperDate))
    .and(Enrolment.SOURCE.eq(PaymentSource.SOURCE_WEB))
    .select(args.context)

    // group enrolments by 'site' which is actually a tag
    Map enrolmentsGrouped = new HashMap<String, List<Enrolment>>()

    queryEnrolments.each { e ->
        Tag centre = e.courseClass.room?.site?.tags.find { it.parentTag.name.equalsIgnoreCase("centres") }

        String groupKey = ""
        if(centre) {
            if (centre.name in centreNames) {
                groupKey = centre.name
            } else {
                groupKey = "Other"
            }
        } else {
            groupKey = "Other"
        }
        if(!enrolmentsGrouped[groupKey]) {
            enrolmentsGrouped[groupKey] = []
        }
        enrolmentsGrouped[groupKey] << e
    }

    enrolmentsGrouped.each { key, enrolments ->
    	String toEmail = "beinfo@communitycollegeni.nsw.edu.au"
    	switch(key) {
    		case "Barraba":
    			toEmail = "barrabacoord@communitycollegeni.nsw.edu.au"
    			break
    		case "Bingara":
    			toEmail = "bingara@communitycollegeni.nsw.edu.au"
    			break
    		case "Gunnedah":
    			toEmail = "gunnedah@communitycollegeni.nsw.edu.au"
    			break
			case "Inverell":
				toEmail = "invadmin@communitycollegeni.nsw.edu.au"
    			break
			case "Moree":
				toEmail = "moree@communitycollegeni.nsw.edu.au"
				break
			case "Narrabri":
				toEmail = "narrabri@communitycollegeni.nsw.edu.au"
				break
			case "Warialda":
				toEmail = "warialda@communitycollegeni.nsw.edu.au "
				break
			case "Other":
				toEmail = "beinfo@communitycollegeni.nsw.edu.au"
				break
    	}

    	String emailBody = "The following online enrolments were accepted for your site for the 24 hours up until midnight " + upperDate.format("dd MMMM YYYY") + ":\n"
    	enrolments.each { e ->
    		emailBody += e.student.contact.firstName + " " + e.student.contact.lastName + " " + e.courseClass.uniqueCode + " [ " + e.courseClass.startDateTime.format("dd MMM YYYY") + " ] " + e.courseClass.course.name +"\n"
    	}
    	email {
    		to toEmail
    		from "theinfo@communitycollegeni.nsw.edu.au"
    		subject key + " enrolment summary: " + upperDate.format("dd MMMM YYYY")
    		content emailBody
    	}

    }




}
