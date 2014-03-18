/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * describes types of concessions available in onCourse
 * 
 * @author marcin
 */
public enum ConcessionTypeEnumeration implements DisplayableExtendedEnumeration<Integer> {

	// this is not an avetmiss field
	NO_CONCESSION(0, "No concession"),
	SENIORS_CARD(1, "Seniors card"),
	STUDENT(2, "Student"),
	PENSIONER(3, "Aged pensioner");

	private String displayName;
	private int value;

	private ConcessionTypeEnumeration(int value, String displayName) {
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
