/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * an enumeration describing student prior education<br/>
 * <br/>
 * AVETMISS - page 77
 */
public enum AvetmissStudentPriorEducation implements DisplayableExtendedEnumeration<Integer> {

	DEFAULT_POPUP_OPTION(0, "not stated"),
	BACHELOR(1, "Bachelor degree or higher degree level"),
	ADVANCED_DIPLOMA(2, "Advanced diploma or associate degree level"),
	DIPLOMA(3, "Diploma level"),
	CERTIFICATE_IV(4, "Certificate IV"),
	CERTIFICATE_III(5, "Certificate III"),
	CERTIFICATE_II(6, "Certificate II"),
	CERTIFICATE_I(7, "Certificate I"),
	MISC(8, "Miscellaneous education"),
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
