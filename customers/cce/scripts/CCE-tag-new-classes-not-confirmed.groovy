/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/**
	Tags new classes an 'Not Confirmed'

	Trigger:
		Entity Event
		CourseClass : Create or Update
*/



def run (args) {

	def cc = args.entity

	if (!cc.hasTag("Programs/Not Confirmed") || !cc.hasTag("Programs/Confirmed") || !cc.hasTag("Programs/Cancelled", true)) {
		cc.addTag("Programs/Not Confirmed")
	}
	args.context.commitChanges()
}
