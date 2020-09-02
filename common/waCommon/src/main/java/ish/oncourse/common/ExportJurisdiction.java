/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.common;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * The AVETMISS export can run in one of several modes, depending on the state it is exported to.
 * It isn't so much a standard as a collection of mutually incompatible standards.
 */
@API
public enum ExportJurisdiction implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * NCVER (Standard AVETMISS)
	 */
	@API
	PLAIN(1, "NCVER (Standard AVETMISS)"),
	
	@Deprecated
	NSW(2, "NSW Department of Education"),
	
	/**
	 * CSO (Community Colleges)
	 */
	@API
	OLIV(3, "CSO (Community Colleges)"),
	
	/**
	 * STSOnline (NSW)
	 */
	@API
	SMART(4, "STSOnline (NSW)"),
	/**
	 * DETConnect (Queensland)
	 */
	@API
	QLD(5, "DETConnect (Queensland)"),
	
	/**
	 * STELA (South Australia)
	 */
	@API
	SA(6, "STELA (South Australia)"),

	/**
	 * Skills Tasmania
	 */
	@API
	TAS(7, "Skills Tasmania"),

	/**
	 * Skills Victoria
	 */
	@API
	VIC(8, "Skills Victoria"),

	/**
	 * STARS (WA)
	 */
	@API
	WA(9, "STARS (WA)"),
	
	@Deprecated
	AQTF(10, "AQTF Competency Completions"),
	
	/**
	 * WA RAPT
	 */
	@API
	RAPT(11, "WA RAPT"),
	
	/**
	 * Northern Territories VET Provider Portal
	 */
	@API
	NTVETPP(12, "Northern Territories VET Provider Portal"),
	
	/**
	 * AVETARS (ACT)
	 */
	@API
	AVETARS(13, "AVETARS (ACT)");

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
