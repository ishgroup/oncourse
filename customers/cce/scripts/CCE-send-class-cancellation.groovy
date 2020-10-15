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

    def natalia = ObjectSelect.query(Contact).where(Contact.EMAIL.eq("natalia.borisova@sydney.edu.au")).selectFirst(args.context)
    def jennifer = ObjectSelect.query(Contact).where(Contact.EMAIL.eq("jennifer.schmitzer@sydney.edu.au")).selectFirst(args.context)
    def cherilyn = ObjectSelect.query(Contact).where(Contact.EMAIL.eq("cherilyn.price@sydney.edu.au")).selectFirst(args.context)
    def jeremy = ObjectSelect.query(Contact).where(Contact.EMAIL.eq("jeremy.mills-sheehy@sydney.edu.au")).selectFirst(args.context)
    def cceAttendant = ObjectSelect.query(Contact).where(Contact.EMAIL.eq("cce.attendant@sydney.edu.au")).selectFirst(args.context)
    def sarah = ObjectSelect.query(Contact).where(Contact.EMAIL.eq("sarah.jessup@sydney.edu.au")).selectFirst(args.context)
    def shona = ObjectSelect.query(Contact).where(Contact.EMAIL.eq("shona.brinley@sydney.edu.au")).selectFirst(args.context)


	def cancelledEnrolments = Enrolment.STATUS.in(EnrolmentStatus.CANCELLED, EnrolmentStatus.REFUNDED).andExp(Enrolment.MODIFIED_ON.gte(last5Mins)).filterObjects(courseClass.getEnrolments())

	/**
	*	If a class is cancelled without having a cancelled childtag applied, attached the 'Cancelled' tag
    *   hasTag(path, isSearchWithChildren) - Check to see whether this object has this tag or it's child tag.
	*/
    courseClass.removeTag("Programs/Confirmed")
    courseClass.removeTag("Programs/Not Confirmed")


    if ( !courseClass.hasTag("Programs/Cancelled", true) ) {
		courseClass.addTag("Programs/Cancelled/Cancelled: Unforeseen Circumstances")
	}

	def t = courseClass.tags.find { Tag tag -> tag.name.startsWith('Cancelled:') }

	/**
	*	Class cancelled email to students enrolled in class
	*	Credit note sent to student by 'send invoice' script <- must be enabled
	*/
	cancelledEnrolments.each { e ->

		email {
            template "CCE Class Cancelled Student"
            to e.student.contact
            cc "jackson@ish.com.au"
            bindings enrolment : e, tag : t
        }
    }

    /**
    *	Email to tutor of class
    */
    courseClass?.tutorRoles.each { tr ->
    	email {
    		template "CCE Class Cancelled Tutor"
    		to tr.tutor.contact
            cc "jackson@ish.com.au"
    		bindings contact : tr.tutor.contact, courseClass : courseClass, tag : t
    	}
    }

    /**
    *	Email to staff group
    */
    email {
        to natalia, jennifer, cherilyn, jeremy, cceAttendant, sarah, shona
    	template "CCE Class Cancelled Staff"
    	bindings courseClass : courseClass, tag : t
    }
    args.context.commitChanges()
}
