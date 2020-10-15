/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def run(args) {
	def application = args.entity

	def recipient_1 = ObjectSelect.query(Contact)
		.where(Contact.EMAIL.eq("mlloyd@sgscc.edu.au"))
		.selectFirst(args.context)

	def recipient_2 = ObjectSelect.query(Contact)
		.where(Contact.EMAIL.eq("slicciardello@sgscc.edu.au"))
		.selectFirst(args.context)

	email {
		template "SGSCC Enrolment application received"
		from preference.email.from
		bindings application : application
		to application.student.contact, recipient_1, recipient_2
	}
}
