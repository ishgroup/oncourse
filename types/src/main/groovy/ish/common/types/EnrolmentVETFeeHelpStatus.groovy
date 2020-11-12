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
 * VET FEE HELP is an Australian government loan programme for funding education. Each enrolment in an eligible FEE HELP class can have one of these states.
 * These states only make sense if the CourseClass has FEE HELP enabled.
 *
 */
@API
public enum EnrolmentVETFeeHelpStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 */
	@API
	NOT_ELIGIBLE(0, "Not eligible", "Not eligible"),

	/**
	 * Database value: 1
	 */
	@API
	HELP_NOT_REQUESTED(1, "HELP not requested", "HELP not requested"),

	/**
	 * Database value: 2
	 */
	@API
	HELP_REQUESTED(2, "HELP requested", "HELP requested");

	private String displayValue;
	private String description;
	private int value;

	private EnrolmentVETFeeHelpStatus(int value, String displayValue, String description) {
		this.displayValue = displayValue;
		this.description = description;
		this.value = value;
	}

	@Override
	public String getDisplayName() {
		return displayValue;
	}

	@Override
	public Integer getDatabaseValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
