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
public enum AvetmissStudentPriorEducation implements DisplayableExtendedEnumeration<Integer> {

	/**
	 */
	@API
	DEFAULT_POPUP_OPTION(0, "not stated"),

	/**
	 */
	@API
	BACHELOR(1, "Bachelor degree or higher degree level"),

	/**
	 */
	@API
	ADVANCED_DIPLOMA(2, "Advanced diploma or associate degree level"),

	/**
	 */
	@API
	DIPLOMA(3, "Diploma level"),

	/**
	 */
	@API
	CERTIFICATE_IV(4, "Certificate IV"),

	/**
	 */
	@API
	CERTIFICATE_III(5, "Certificate III"),

	/**
	 */
	@API
	CERTIFICATE_II(6, "Certificate II"),

	/**
	 */
	@API
	CERTIFICATE_I(7, "Certificate I"),

	/**
	 */
	@API
	MISC(8, "Miscellaneous education"),

	/**
	 */
	@API
	NONE(100, "None");

	private String displayName;
	private int value;

	private AvetmissStudentPriorEducation(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
