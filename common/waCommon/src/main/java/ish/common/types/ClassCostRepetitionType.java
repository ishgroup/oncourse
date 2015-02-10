/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Some class costs can be repeated multiple times for the one class. If these repetitions are attached
 * to a payroll amount, then these repeating payroll entries will be created.
 * 
 * @PublicApi
 */
public enum ClassCostRepetitionType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * A fixed amount. For payroll, the amount will be paid once the first session has been delivered.
	 * Database value: 1
	 * 
	 * @PublicApi
	 */
	FIXED(1, "Fixed", "", ""),
 
	/**
	 * This cost is a fixed amount per session. This is common for room hire where the amount doesn't depend on the duration of the session.
	 * Database value: 2
	 * 
	 * @PublicApi
	 */
	PER_SESSION(2, "Per session", "sessions", "session"),

	/**
	 * A cost per enrolment (like class materials).
	 * Database value: 3
	 * 
	 * @PublicApi
	 */
	PER_ENROLMENT(3, "Per enrolment", "enrolments", "enrolment"),
 
	/**
	 * This is just like the fixed amount but with a multiplier again an arbitrary number of units.
	 * Database value: 4
	 * 
	 * @PublicApi
	 */
	PER_UNIT(4, "Per unit", "hrs", "hr"),
 
	/**
	 * Discounts are a special type. You can't directly create these sorts of costs, but they are automatically
	 * created for you when you attach a discount to a class.
	 * Database value: 5
	 * 
	 * @PublicApi
	 */
	DISCOUNT(5, "Discount", "discounts", "discount"), // remove from payrate

	/**
	 * Total session duration. For wages, this amount is the payable session hours, not the actual hours.
	 * Database value: 6
	 * @PublicApi
	 */
	PER_TIMETABLED_HOUR(6, "Per timetabled hour", "hrs", "hr"), // using date session

	/**
	 * Timetabled hours multiplied by the number of enrolments
	 * Database value: 6
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
