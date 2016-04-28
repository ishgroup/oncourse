/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
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
public enum ClassFundingSource implements DisplayableExtendedEnumeration<Integer>, AvetmissReportingEnum {

	/**
	 * Database value: 0
	 */
	@API
	COMMONWEALTH_AND_STATE_GENERAL(0, "Commonwealth and state - general recurrent", "11"),

	/**
	 * Database value: 1
	 */
	@API
	COMMONWEALTH_SPECIFIC(1, "Commonwealth - specific", "13"),

	/**
	 * Database value: 2
	 */
	@API
	STATE_SPECIFIC(2, "State - specific", "15"),

	/**
	 * Database value: 3
	 */
	@API
	DOMESTIC_FULL_FEE(3, "Domestic full fee paying student", "20"),

	/**
	 * Database value: 4
	 */
	@API
	INTERNATIONAL_FULL_FEE(4, "International full fee paying student", "30"),

	/**
	 * Database value: 5
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

	/**
	 * @see ish.common.types.AvetmissReportingEnum#getAvetmissCode()
	 */
	@Override
	public String getAvetmissCode() {
		return this.avetmissCode;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}