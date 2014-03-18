/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.common;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * The AVETMISS export can run in one of several modes, depending on the state it is exported to. It isn't so much a standard as a collection of mutually
 * incompatible standards.
 */
public enum ExportJurisdiction implements DisplayableExtendedEnumeration<Integer> {
	PLAIN(1, "AVETMISS standard"),
	NSW(2, "NSW Department of Education"),
	QLD(3, "QLD"),
	SA(4, "SA"),
	TAS(5, "TAS (Skills Tasmania)"),
	VIC(6, "VIC (Skills Victoria Training System)"),
	WA(7, "WA"),
	AQTF(8, "AQTF Competency Completions"),
	RAPT(9, "WA RAPT");

	private Integer value;
	private String displayName;

	private ExportJurisdiction(Integer value, String displayName) {
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

	/**
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return getDisplayName();
	}
}
