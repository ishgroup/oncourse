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
 * Some tags have a special meaning. Because tags were originally called "nodes" in onCourse, some of the old
 * terminology still lingers.
 *
 */
@API
public enum NodeSpecialType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 1
	 *
	 * There can only be one tag group called "subjects" and there must always be one.
	 * It has special meaning in an onCourse website.
	 * Tag is used to filter cources on the college website
	 */
	@API
	SUBJECTS(1, "Subjects"),

	@Deprecated
	HOME_WEBPAGE(2, "Home web page"),

	/**
	 * Database value: 3
	 *
	 * Tag groups which are of type mailing list appear in a special place in the user interface
	 * but are still just a flag against a contact showing they are part of a mailing list.
	 */
	@API
	@Deprecated
	MAILING_LISTS(3, "Mailing lists"),

	/**
	 * Database value: 4
	 *
	 * Wage intervals are a tag group used to designate pay cycles.
	 */
	@API
	PAYROLL_WAGE_INTERVALS(4, "Payroll wage intervals"),

	/**
	 * Database value: 5
	 *
	 * Assessment methods are a tag group used to mark assessments.
	 */
	@API
	ASSESSMENT_METHOD(5, "Assessment methods"),

	/**
	 * Database value: 6
	 *
	 * There can only be one tag group called "terms" and there must always be one.
	 * It has special meaning in an onCourse website.
	 * Tag is used to filter classes on the college website
	 */
	@API
	TERMS(6, "Terms"),

	/**
	 * Database value: 7
	 *
	 * There can only be one tag group called "Class extended types"
	 * It has special meaning in an onCourse website.
	 * Tag is used to filter classes on the college website
	 */
	@API
	CLASS_EXTENDED_TYPES(7, "Class extended types"),

	/**
	 * Database value: 8
	 *
	 * There can only be one tag group called "Course extended types" and there must always be one.
	 * It has special meaning in an onCourse website.
	 * Tag is used to filter classes on the college website
	 */
	@API
	COURSE_EXTENDED_TYPES(8, "Course extended types")

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
