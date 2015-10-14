/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * A basic workflow for applications. Each application moves through the statuses, resulting in
 * scripts firing off emails and different behaviour in the skillsOnCourse portal.
 * 
 * Tags can be used for more detailed workflow and tracking of progress.
 * 
 */
@API
public enum ApplicationStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 */
	@API
	NEW(0, "New"),

	/**
	 * Database value: 1
	 */
	@API
	OFFERED(1, "Offered"),

	/**
	 * Database value: 2
	 */
	@API
	ACCEPTED(2, "Accepted"),

	/**
	 * Database value: 3
	 */
	@API
	REJECTED(3, "Rejected"),

	/**
	 * Database value: 4
	 */
	@API
	WITHDRAWN(4, "Withdrawn");

	private String displayName;
	private int value;

	private ApplicationStatus(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}
	
	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
