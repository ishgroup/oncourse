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
 * Some class costs can be repeated multiple times for the one class. If these repetitions are attached
 * to a payroll amount, then these repeating payroll entries will be created.
 *
 */
@API
public enum ClassCostRepetitionType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 1
	 *
	 * A fixed amount. For payroll, the amount will be paid once the first session has been delivered.
	 */
	@API
	FIXED(1, "Fixed", "", ""),

	/**
	 * Database value: 2
	 *
	 * This cost is a fixed amount per session. This is common for room hire where the amount doesn't depend on the duration of the session.
	 */
	@API
	PER_SESSION(2, "Per session", "sessions", "session"),

	/**
	 * Database value: 3
	 *
	 * A cost per enrolment (like class materials).
	 */
	@API
	PER_ENROLMENT(3, "Per enrolment", "enrolments", "enrolment"),

	/**
	 * Database value: 4
	 *
	 * This is just like the fixed amount but with a multiplier against an arbitrary number of units.
	 */
	@API
	PER_UNIT(4, "Per unit", "hrs", "hr"),

	/**
	 * Database value: 5
	 *
	 * Discounts are a special type. You can't directly create these sorts of costs, but they are automatically
	 * created for you when you attach a discount to a class.
	 */
	@API
	DISCOUNT(5, "Discount", "discounts", "discount"), // remove from payrate

	/**
	 * Database value: 6
	 *
	 * Total session duration. For wages, this amount is the payable session hours, not the actual hours.
	 */
	@API
	PER_TIMETABLED_HOUR(6, "Per timetabled hour", "hrs", "hr"), // using date session

	/**
	 * Database value: 7
	 *
	 * Timetabled hours multiplied by the number of enrolments
	 */
	@API
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
