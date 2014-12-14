/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * A basic workflow for applications. Each application moves through the statuses, resulting in
 * scripts firing off emails and different behaviour in the skillsOnCourse portal.
 * 
 * Tags can be used for more detailed workflow and tracking of progress.
 * 
 * @PublicApi
 */
public enum ApplicationStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	NEW(0, "New"),

	/**
	 * @PublicApi
	 */
	OFFERED(1, "Offered"),

	/**
	 * @PublicApi
	 */
	ACCEPTED(2, "Accepted"),

	/**
	 * @PublicApi
	 */
	REJECTED(3, "Rejected"),

	/**
	 * @PublicApi
	 */
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
