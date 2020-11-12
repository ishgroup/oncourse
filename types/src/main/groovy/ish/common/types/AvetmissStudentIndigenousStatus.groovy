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
 * A set of values for AVETMISS reporting from the version 7 standard.
 * Consult the AVETMISS documentation for more detail about these options.
 *
 */
@API
public enum AvetmissStudentIndigenousStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0 Not stated
	 */
	@API
	DEFAULT_POPUP_OPTION(0, "not stated"),

	/**
	 * Database value: 1 Aboriginal
	 */
	@API
	ABORIGINAL(1, "Aboriginal"),

	/**
	 * Database value: 2 Torres Strait Islander
	 */
	@API
	TORRES(2, "Torres Strait Islander"),

	/**
	 * Database value: 3 Aboriginal and Torres Strait Islander
	 */
	@API
	ABORIGINAL_AND_TORRES(3, "Aboriginal and Torres Strait Islander"),

	/**
	 * Database value: 4 Neither
	 */
	@API
	NEITHER(4, "Neither");

	private String displayName;
	private int value;

	private AvetmissStudentIndigenousStatus(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDatabaseValue()
	 */
	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
