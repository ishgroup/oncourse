/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Attendance of a student or tutor at a session can be given a status.
 * 
 * @PublicApi
 */
public enum AttendanceType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * This attendance record has not yet been marked.
	 * 
	 * Database value: 0
	 * @PublicApi
	 */
	UNMARKED(0, "Unmarked"),
	
	/**
	 * The student or tutor attended the session in full.
	 * 
	 * Database value: 1
	 * @PublicApi
	 */
	ATTENDED(1, "Attended"),

	/**
	 * The student or tutor was absent and gave a suitable reason.
	 * 
	 * Database value: 2
	 * @PublicApi
	 */
	DID_NOT_ATTEND_WITH_REASON(2, "Absent with reason"),

	/**
	 * The student or tutor was absent without good cause.
	 * 
	 * Database value: 3
	 * @PublicApi
	 */
	DID_NOT_ATTEND_WITHOUT_REASON(3, "Absent without reason"),

	/**
	 * The student or tutor attended, but for less than the full duration. The attendance
	 * duration can be stored in another attribute in the Attendance record.
	 * 
	 * Database value: 4
	 * @PublicApi
	 */
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
