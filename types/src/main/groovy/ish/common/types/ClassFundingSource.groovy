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
public enum ClassFundingSource implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 *
	 * Commonwealth and state - general recurrent
	 */
	@API
	COMMONWEALTH_AND_STATE_GENERAL(0, "Commonwealth and state - general recurrent", "11"),

	/**
	 * Database value: 1
	 *
	 * Commonwealth - specific
	 */
	@API
	COMMONWEALTH_SPECIFIC(1, "Commonwealth - specific", "13"),

	/**
	 * Database value: 2
	 *
	 * State - specific
	 */
	@API
	STATE_SPECIFIC(2, "State - specific", "15"),

	/**
	 * Database value: 3
	 *
	 * Domestic full fee paying student
	 */
	@API
	DOMESTIC_FULL_FEE(3, "Domestic full fee paying student", "20"),

	/**
	 * Database value: 4
	 *
	 * International full fee paying student.
	 * Revenue provided by or for an international client to undertake education and training and who temporarily
	 * resides in Australia and holds a student visa or a temporary residency permit, or who resides in an overseas
	 * country and whose funding source does not come from any of the other funding categories
	 */
	@API
	INTERNATIONAL_FULL_FEE(4, "International full fee paying student", "30"),

	/**
	 * Database value: 6
	 *
	 * International onshore client.
	 * Revenue provided by or for an international client to undertake education and training and who temporarily
	 * resides in Australia and holds a student visa or a temporary residency permit and whose funding source does
	 * not come from any of the other funding categories
	 */
	@API
	INTERNATIONAL_ONSHORE(6, "International onshore client", "31"),

	/**
	 * Database value: 7
	 *
	 * International offshore client.
	 * Revenue provided by or for an international client to undertake education and training and who resides in an
	 * overseas country and whose funding source does not come from any of the other funding categories
	 */
	@API
	INTERNATIONAL_OFFSHORE(7, "International offshore client", "32"),

	/**
	 * Database value: 5
	 *
	 * Revenue from other RTO
	 */
	@API
	REVENUE_FROM_OTHER_TO(5, "Revenue from other RTO", "80");

	private String displayName;
	private int value;
	private String avetmissCode;

	private ClassFundingSource(int value, String displayName, String avetmissCode) {
		this.value = value;
		this.displayName = displayName;
		this.avetmissCode = avetmissCode;
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

	public String getAvetmissCode() {
		return this.avetmissCode;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
