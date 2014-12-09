/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * @PublicApi
 */
public enum ClassFundingSource implements DisplayableExtendedEnumeration<Integer>, AvetmissReportingEnum {

	/**
	 * @PublicApi
	 */
	COMMONWEALTH_AND_STATE_GENERAL(0, "Commonwealth and state - general recurrent", "11"),

	/**
	 * @PublicApi
	 */
	COMMONWEALTH_SPECIFIC(1, "Commonwealth - specific", "13"),

	/**
	 * @PublicApi
	 */
	STATE_SPECIFIC(2, "State - specific", "15"),

	/**
	 * @PublicApi
	 */
	DOMESTIC_FULL_FEE(3, "Domestic full fee paying student", "20"),

	/**
	 * @PublicApi
	 */
	INTERNATIONAL_FULL_FEE(4, "International full fee paying student", "30"),

	/**
	 * @PublicApi
	 */
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