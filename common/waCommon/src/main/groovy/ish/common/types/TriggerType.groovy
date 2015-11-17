/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * Each script can be triggered in a particular way. This enum defined how the trigger is fired.
 */
@API
public enum TriggerType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * This script will be executed at predefined times (as expressed in a cron string).
	 */
	@API
	CRON(1, "Cron"),

	/**
	 * This script will be executed when a database object is created, edited or deleted.
	 */
	@API
	ENTITY_EVENT(2, "Entity event"),

	/**
	 * This script will be executed on particular lifecycle events such as when an enrolment becomes confirmed or a class is cancelled.
	 */
	@API
	ONCOURSE_EVENT(3, "onCourse event"),

	/**
	 * This script will only be executed when a user manually causes it to run.
	 */
	@API
	ON_DEMAND(4, "On demand");

	private int value;
	private String displayName;

	private TriggerType(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public Integer getDatabaseValue() {
		return value;
	}

}
