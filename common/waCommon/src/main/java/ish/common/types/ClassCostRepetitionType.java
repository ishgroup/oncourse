/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * This enum lists possible options for class cost. based on the type of enum the actual and budgeted costs are calculated.
 * 
 * @PublicApi
 */
public enum ClassCostRepetitionType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * a fixed sum
	 * 
	 * @PublicApi
	 */
	FIXED(1, "Fixed", "", ""),
 
	/**
	 * unit is a session
	 * 
	 * @PublicApi
	 */
	PER_SESSION(2, "Per session", "sessions", "session"),

	/**
	 * unit is an enrolment
	 * 
	 * @PublicApi
	 */
	PER_ENROLMENT(3, "Per enrolment", "enrolments", "enrolment"),
 
	/**
	 * unit is an hour, but the number of units is specified arbitrarily
	 * 
	 * @PublicApi
	 */
	PER_UNIT(4, "Per unit", "hrs", "hr"),
 
	/**
	 * unit is a discount
	 * 
	 * @PublicApi
	 */
	DISCOUNT(5, "Discount", "discounts", "discount"), // remove from payrate

	/**
	 * unit is a classroom hour, or for wages payable hour (see relevant methods in class/session)
	 * 
	 * @PublicApi
	 */
	PER_TIMETABLED_HOUR(6, "Per timetabled hour", "hrs", "hr"), // using date session

	/**
	 * unit is as timetabled hour * number of enrolments
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
