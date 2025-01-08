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
package ish.oncourse.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

@API
public enum WorkingWithChildrenStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 * No application was lodged to check status
	 */
	@API
	NOT_CHECKED(0, "Not checked"),

	/**
	 * Database value: 1
	 * Application was lodged but no response yet
	 */
	@API
	APPLICATION_IN_PROGRESS(1, "Application in progress"),

	/**
	 * Database value: 2
	 * Application successful
	 */
	@API
	CLEARED(2, "Cleared"),

	/**
	 * Database value: 3
	 * Application response prevents tutor working with children
	 */
	@API
	BARRED(3, "Barred"),

	/**
	 * Database value: 4
	 * Application response prevents tutor working with children until a full check can be returned.
	 */
	@API
	INTERIM_BARRED(4, "Interim barred");

	private final int value;
	private final String displayName;

	WorkingWithChildrenStatus(int value, String displayName) {
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
