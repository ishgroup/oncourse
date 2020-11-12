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

package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API


/**
 * A set of values
 */
@API
public enum ContactDuplicateStatus implements DisplayableExtendedEnumeration<Integer> {

	@API
	POSSIBLE_MATCH(0, "Possible match"),

	@API
	REJECTED_MATCH(1, "Rejected match"),
	@API
	IN_TRANSACTION(2, "In transaction"),
	@API
	PROCESSED(3, "Processed");

	private String displayName;
	private int value;

	private ContactDuplicateStatus(int value, String displayValue) {
		this.displayName = displayValue;
		this.value = value;
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
