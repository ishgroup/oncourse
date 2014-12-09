package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * enumeration of valid student citizen/resident indicator
 * 
 * @PublicApi
 */
public enum StudentCitizenship implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	AUSTRALIAN_CITIZEN(1, "Australian citizen",
		"Australian citizen (including Australian citizens with dual citizenship)"),

	/**
	 * @PublicApi
	 */
	NEW_ZELAND_CITIZEN(2, "New Zealand citizen",
		"New Zealand citizen or a diplomatic or consular representative of New Zealand, a member of the staff of such a representative or the spouse or dependent relative of such a representative, excluding those with Australian citizenship. (Note: includes any such persons who have Permanent Resident status)"),

	/**
	 * @PublicApi
	 */
	STUDENT_WITH_PERMANENT_VISA(3, "Students/Applicants with permanent visa",
		"Students/Applicants with permanent visa other than permanent humanitarian visa"),

	/**
	 * @PublicApi
	 */
	STUDENT_WITH_TEMPORARY_ENTRY_PERMIT(4, "Student/Applicant has a temporary entry permit",
		"Student/Applicant has a temporary entry permit or is a diplomat or a dependent of a diplomat (except New Zealand) and resides in Australia during the unit of study"),

	/**
	 * @PublicApi
	 */
	NONE_OF_THE_ABOVE_CATEGORIES(5, "Not one of the above categories",
		"Not one of the above categories and student/applicant is residing outside Australia during the unit of study/time of application"),

	/**
	 * @PublicApi
	 */
	STUDENT_WITH_PERMANENT_HUMANITARIAN_VISA(8, "Students/Applicants with permanent humanitarian visa",
		"Students/Applicants with permanent humanitarian visa"),

	/**
	 * @PublicApi
	 */
	NO_INFORMATION(9, "No information", "No information");

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
