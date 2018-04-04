/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * A USI can be validated against the government verification service.
 */
@API
public enum UsiStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
     * Database value: 0
     * The USI has not been entered at all.
	 */
	@API
	DEFAULT_NOT_SUPPLIED(0, "Not supplied"),

	/**
     * Database value: 1
     * A verification was attempted, but it failed.
	 */
	@API
	NON_VERIFIED(1, "Not verified"),

	/**
     * Database value: 2
     * A verification was made and succeeded.
	 */
	@API
	VERIFIED(2, "Verified"),

	/**
	 * Database value: 3
	 * A student objected to having a USI and obtained an exemption.
	 */
	@API
	EXEMPTION(3, "Exemption"),

	/**
	 * Database value: 4
	 * A student did their training offshore and isn't required to get a USI
	 */
	@API
	INTERNATIONAL(4, "International")

	private String displayName;
	private int value;

	private UsiStatus(int value, String displayName) {
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
