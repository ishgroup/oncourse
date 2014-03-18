/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * enumeration of available attendance types
 * 
 * @author thomas
 */
public enum AttendanceType implements DisplayableExtendedEnumeration<Integer> {
	UNMARKED(0, "Unmarked"),
	ATTENDED(1, "Attended"),
	DID_NOT_ATTEND_WITH_REASON(2, "Absent with reason"),
	DID_NOT_ATTEND_WITHOUT_REASON(3, "Absent without reason"),
	PARTIAL(4, "Partial");

	private final int value;
	private String displayName;

	private AttendanceType(int value, String displayName) {
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
