/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
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
	 * Database value: 1
	 *
	 * This script will be executed at predefined times (as expressed in a cron string).
	 */
	@API
	CRON(1, "Cron"),

	/**
	 * Database value: 2
	 *
	 * This script will be executed when a database object is created, edited or deleted.
	 */
	@API
	ENTITY_EVENT(2, "Entity event"),

	/**
	 * Database value: 3
	 *
	 * This script will be executed on particular lifecycle events such as when an enrolment becomes confirmed or a class is cancelled.
	 */
	@API
	ONCOURSE_EVENT(3, "onCourse event"),

	/**
	 * Database value: 4
	 *
	 * This script will only be executed when user manually runs it.
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
