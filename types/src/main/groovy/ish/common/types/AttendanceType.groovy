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
 * Attendance of a student or tutor at a session can be given a status.
 *
 */
@API
public enum AttendanceType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * This attendance record has not yet been marked.
	 *
	 * Database value: 0
	 */
	@API
	UNMARKED(0, "Unmarked"),

	/**
	 * The student or tutor attended the session in full.
	 *
	 * Database value: 1
	 */
	@API
	ATTENDED(1, "Attended"),

	/**
	 * The student or tutor was absent and gave a suitable reason.
	 *
	 * Database value: 2
	 */
	@API
	DID_NOT_ATTEND_WITH_REASON(2, "Absent with reason"),

	/**
	 * The student or tutor was absent without good cause.
	 *
	 * Database value: 3
	 */
	@API
	DID_NOT_ATTEND_WITHOUT_REASON(3, "Absent without reason"),

	/**
	 * The student or tutor attended, but for less than the full duration. The attendance
	 * duration can be stored in another attribute in the Attendance record.
	 *
	 * Database value: 4
	 */
	@API
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

	public static final List<AttendanceType> STATUSES_ABSENCE = Arrays.asList(DID_NOT_ATTEND_WITHOUT_REASON,
			DID_NOT_ATTEND_WITH_REASON);
}
