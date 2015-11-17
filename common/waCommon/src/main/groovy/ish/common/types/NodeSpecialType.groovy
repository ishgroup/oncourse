/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * Some tags have a special meaning. Because tags were originally called "nodes" in onCourse, some of the old
 * terminology still lingers.
 * 
 */
@API
public enum NodeSpecialType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * There can only be one tag group called "subjects" and there must always be one.
	 * It has special meaning in an onCourse website.
	 * 
	 */
	@API
	SUBJECTS(1, "Subjects"),

	@Deprecated
	HOME_WEBPAGE(2, "Home web page"),

	/**
	 * Tag groups which are of type mailing list appear in a special place in the user interface
	 * but are still just a flag against a contact showing they are part of a mailing list.
	 * 
	 */
	@API
	MAILING_LISTS(3, "Mailing lists"),

	/**
	 * Wage intervals are a tag group used to designate pay cycles. 
	 * 
	 */
	@API
	PAYROLL_WAGE_INTERVALS(4, "Payroll wage intervals");

	private String displayName;
	private int value;

	private NodeSpecialType(int value, String displayName) {
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
