/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Some class costs can be repeated multiple times for the one class.
 * 
 * @PublicApi
 */
public enum ClassCostRepetitionType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * a fixed amount
	 * 
	 * @PublicApi
	 */
	FIXED(1, "Fixed", "", ""),
 
	/**
	 * This cost is incurred for every session (such as room hire).
	 * 
	 * @PublicApi
	 */
	PER_SESSION(2, "Per session", "sessions", "session"),

	/**
	 * A cost per enrolment (like class materials)
	 * 
	 * @PublicApi
	 */
	PER_ENROLMENT(3, "Per enrolment", "enrolments", "enrolment"),
 
	/**
	 * Any quantity you like. For example, you can budget for 4 hours of cleaning at the end of a class.
	 * 
	 * @PublicApi
	 */
	PER_UNIT(4, "Per unit", "hrs", "hr"),
 
	/**
	 * Discounts are a special type.
	 * 
	 * @PublicApi
	 */
	DISCOUNT(5, "Discount", "discounts", "discount"), // remove from payrate

	/**
	 * Total session duration. For wages, this amount is the payable session hours, not the actual hours.
	 * 
	 * @PublicApi
	 */
	PER_TIMETABLED_HOUR(6, "Per timetabled hour", "hrs", "hr"), // using date session

	/**
	 * Timetabled hours multiplied by the number of enrolments
	 * 
	 * @PublicApi
	 */
	PER_STUDENT_CONTACT_HOUR(7, "Per student contact hour", "hrs", "hr");

	private String displayName;
	private String multipleUnitsName;
	private String singularUnitName;
	private int value;

	private ClassCostRepetitionType(int value, String displayName, String multipleUnitsName, String singularUnitName) {
		this.value = value;
		this.displayName = displayName;
		this.multipleUnitsName = multipleUnitsName;
		this.singularUnitName = singularUnitName;
	}

	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * @return name of the repetition units in plural form
	 */
	public String getMultipleUnitsName() {
		return this.multipleUnitsName;
	}

	/**
	 * @return name of the repetition units in singular form
	 */
	public String getSingularUnitName() {
		return this.singularUnitName;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
