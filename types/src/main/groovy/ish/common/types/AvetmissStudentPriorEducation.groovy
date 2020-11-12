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
public enum AvetmissStudentPriorEducation implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0 Not stated
	 */
	@API
	DEFAULT_POPUP_OPTION(0, "not stated"),

	/**
	 * Database value: 1 Bachelor degree or higher degree level
	 */
	@API
	BACHELOR(1, "Bachelor degree or higher degree level"),

	/**
	 * Database value: 2 Advanced diploma or associate degree level
	 */
	@API
	ADVANCED_DIPLOMA(2, "Advanced diploma or associate degree level"),

	/**
	 * Database value: 3 Diploma level
	 */
	@API
	DIPLOMA(3, "Diploma level"),

	/**
	 * Database value: 4 Certificate IV
	 */
	@API
	CERTIFICATE_IV(4, "Certificate IV"),

	/**
	 * Database value: 5 Certificate III
	 */
	@API
	CERTIFICATE_III(5, "Certificate III"),

	/**
	 * Database value: 6 Certificate II
	 */
	@API
	CERTIFICATE_II(6, "Certificate II"),

	/**
	 * Database value: 7 Certificate I
	 */
	@API
	CERTIFICATE_I(7, "Certificate I"),

	/**
	 * Database value: 8 Miscellaneous education
	 */
	@API
	MISC(8, "Miscellaneous education"),

	/**
	 * Database value: 100 None
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
