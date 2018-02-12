/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API

/**
 * A basic workflow for applications. Each application moves through the statuses, resulting in
 * scripts firing off emails and different behaviour in the skillsOnCourse portal.
 * 
 * Tags can be used for more detailed workflow and tracking of progress.
 * 
 */
@API
enum ApplicationStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 *
	 * Student applied for the class. Application is up for reviewing by college.
	 */
	@API
	NEW(0, "New"),

	/**
	 * Database value: 1
	 *
	 * College reviewed NEW student application and allowed an enrolment to class.
	 */
	@API
	OFFERED(1, "Offered"),

	/**
	 * Database value: 2
	 *
	 * Set automatically once student with OFFERED application enrol to the class.
	 */
	@API
	ACCEPTED(2, "Accepted"),

	/**
	 * Database value: 3
	 *
	 * College reviewed NEW student application and rejected it.
	 */
	@API
	REJECTED(3, "Rejected"),

	/**
	 * Database value: 4
	 *
	 * OFFERED application was rejected by student.
	 */
	@API
	WITHDRAWN(4, "Withdrawn"),

	/**
	 * Database value: 5
	 *
	 * Status of un-actioned applications and new applications which are being 
	 * assessed, but a judgement to offer or reject has not yet been made.
	 */
	@API
	IN_PROGRESS(5, "In progress")
	

	private String displayName
	private int value

	private ApplicationStatus(int value, String displayName) {
		this.value = value
		this.displayName = displayName
	}
	
	@Override
	Integer getDatabaseValue() {
		return this.value
	}

	@Override
	String getDisplayName() {
		return this.displayName
	}

	@Override
	String toString() {
		return getDisplayName()
	}
}
