/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def run(args) {
	def courseClass = args.entity

	// For now assume there is only one tutor
	def tutor = courseClass.tutorRoles[0]?.tutor

	email {
		to tutor.contact
		from "vicki@byroncollege.org.au"
		template "tutor contract"
		bindings tutor: tutor, cc: courseClass
	}
}
