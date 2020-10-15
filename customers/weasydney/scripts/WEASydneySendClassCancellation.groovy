/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import groovy.time.TimeCategory


def run(args) {
    def courseClass = args.entity

	def last5Mins
	use( TimeCategory ) {
		last5Mins = new Date() - 5.minutes
	}

	def cancelledEnrolments = Enrolment.STATUS.in(EnrolmentStatus.CANCELLED, EnrolmentStatus.REFUNDED).andExp(Enrolment.MODIFIED_ON.gte(last5Mins)).filterObjects(courseClass.getEnrolments())

	def noMobileContacts = ""
	def courseName = cancelledEnrolments.first().courseClass.course.name + " " + cancelledEnrolments.first().courseClass.uniqueCode

	cancelledEnrolments.each { e ->
		email {
            template "Class Cancellation"
            to e.student.contact
            bindings enrolment : e
        }

        if (e.student.contact.mobilePhone != null && e.student.contact.mobilePhone != "") {
        	sms {
        		to e.student.contact
				text "We are sorry to notify you that ${e.courseClass.course.name} has been cancelled. We will email you details of your refund.".take(160)
        	}
        } else {
        	noMobileContacts += "${e.student.contact.fullName} H:${e.student.contact?.homePhone} W:${e.student.contact?.workPhone} \n"
        }
    }

    if (noMobileContacts) {
    	email {
    		to "james.laughlin@weasydney.nsw.edu.au"
    		from "support@weasydney.com.au"
    		subject "Class ${courseName} cancelled: Students without mobile numbers"
    		content "Dear admin team, \n"\
    		+ "${courseName} has been cancelled. Students with an email and mobile contact have already been notified. \n"\
    		+ "The following students do not have emails or mobile numbers and you will need to contact them.\n"\
    		+ noMobileContacts + "Best Regards"
    	}
    }
}
