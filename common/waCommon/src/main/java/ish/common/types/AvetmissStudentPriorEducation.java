/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * A set of values for AVETMISS reporting from the version 7 standard.
 * Consult the AVETMISS documentation for more detail about these options.
 * 
 * @PublicApi
 */
public enum AvetmissStudentPriorEducation implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	DEFAULT_POPUP_OPTION(0, "not stated"),

	/**
	 * @PublicApi
	 */
	BACHELOR(1, "Bachelor degree or higher degree level"),

	/**
	 * @PublicApi
	 */
	ADVANCED_DIPLOMA(2, "Advanced diploma or associate degree level"),

	/**
	 * @PublicApi
	 */
	DIPLOMA(3, "Diploma level"),

	/**
	 * @PublicApi
	 */
	CERTIFICATE_IV(4, "Certificate IV"),

	/**
	 * @PublicApi
	 */
	CERTIFICATE_III(5, "Certificate III"),

	/**
	 * @PublicApi
	 */
	CERTIFICATE_II(6, "Certificate II"),

	/**
	 * @PublicApi
	 */
	CERTIFICATE_I(7, "Certificate I"),

	/**
	 * @PublicApi
	 */
	MISC(8, "Miscellaneous education"),

	/**
	 * @PublicApi
	 */
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
