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
 * A set of values for AVETMISS reporting from the version 7 standard.
 * Consult the AVETMISS documentation for more detail about these options.
 *
 */
@API
public enum StudentCitizenship implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 1
	 *
	 * Australian citizen (including Australian citizens with dual citizenship)
	 */
	@API
	AUSTRALIAN_CITIZEN(1, "Australian citizen",
		"Australian citizen (including Australian citizens with dual citizenship)"),

	/**
	 * Database value: 2
	 *
	 * New Zealand citizen or a diplomatic or consular representative of New Zealand, a member of the staff of such a representative or the spouse or dependent relative of such a representative, excluding those with Australian citizenship. (Note: includes any such persons who have Permanent Resident status)
	 */
	@API
	NEW_ZELAND_CITIZEN(2, "New Zealand citizen",
		"New Zealand citizen or a diplomatic or consular representative of New Zealand, a member of the staff of such a representative or the spouse or dependent relative of such a representative, excluding those with Australian citizenship. (Note: includes any such persons who have Permanent Resident status)"),

	/**
	 * Database value: 3
	 *
	 * Students/Applicants with permanent visa other than permanent humanitarian visa
	 */
	@API
	STUDENT_WITH_PERMANENT_VISA(3, "Students/Applicants with permanent visa",
		"Students/Applicants with permanent visa other than permanent humanitarian visa"),

	/**
	 * Database value: 4
	 *
	 * Student/Applicant has a temporary entry permit or is a diplomat or a dependent of a diplomat (except New Zealand) and resides in Australia during the unit of study
	 */
	@API
	STUDENT_WITH_TEMPORARY_ENTRY_PERMIT(4, "Student/Applicant has a temporary entry permit",
		"Student/Applicant has a temporary entry permit or is a diplomat or a dependent of a diplomat (except New Zealand) and resides in Australia during the unit of study"),

	/**
	 * Database value: 5
	 *
	 * Not one of the above categories and student/applicant is residing outside Australia during the unit of study/time of application
	 */
	@API
	NONE_OF_THE_ABOVE_CATEGORIES(5, "Not one of the above categories",
		"Not one of the above categories and student/applicant is residing outside Australia during the unit of study/time of application"),

	/**
	 * Database value: 8
	 *
	 * Students/Applicants with permanent humanitarian visa
	 */
	@API
	STUDENT_WITH_PERMANENT_HUMANITARIAN_VISA(8, "Students/Applicants with permanent humanitarian visa",
		"Students/Applicants with permanent humanitarian visa"),

	/**
	 * Database value: 9
	 *
	 * No information
	 */
	@API
	NO_INFORMATION(9, "No information", "No information"),

	@API
	STUDENT_WITH_PACIFIC_ENGAGEMENT_VISA(10, "Students/Applicants with Pacific Engagement Visa (PEV)", "Students/Applicants with Pacific Engagement Visa (PEV)");

	private String displayValue;
	private String description;
	private int value;

	private StudentCitizenship(int value, String displayValue, String description) {
		this.displayValue = displayValue;
		this.value = value;
		this.description = description;
	}

	@Override
	public Integer getDatabaseValue() {
		return value;
	}

	@Override
	public String getDisplayName() {
		return displayValue;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
