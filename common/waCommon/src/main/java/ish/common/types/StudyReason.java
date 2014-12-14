/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * A set of values for AVETMISS reporting from the version 7 standard.
 * Consult the AVETMISS documentation for more detail about these options.
 * @PublicApi
 */
public enum StudyReason implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	STUDY_REASON_NOT_STATED(-1, "Not stated"),

	/**
	 * @PublicApi
	 */
	STUDY_REASON_JOB(0, "To get a job"),

	/**
	 * @PublicApi
	 */
	STUDY_REASON_DEVELOP_BUSINESS(1, "To develop my existing business"),

	/**
	 * @PublicApi
	 */
	STUDY_REASON_START_BUSINESS(2, "To start my own business"),

	/**
	 * @PublicApi
	 */
	STUDY_REASON_CAREER_CHANGE(4, "To try for a different career"),

	/**
	 * @PublicApi
	 */
	STUDY_REASON_BETTER_JOB(8, "To get a better job or promotion"),

	/**
	 * @PublicApi
	 */
	STUDY_REASON_JOB_REQUIREMENT(16, "It was a requirement of my job"),

	/**
	 * @PublicApi
	 */
	STUDY_REASON_EXTRA_JOB_SKILLS(32, "I wanted extra skills for my job"),

	/**
	 * @PublicApi
	 */
	STUDY_REASON_FOR_ANOTHER_COURSE(64, "To get into another course of study"),

	/**
	 * @PublicApi
	 */
	STUDY_REASON_OTHER(128, "Other reasons"),

	/**
	 * @PublicApi
	 */
	STUDY_REASON_PERSONAL_INTEREST(256, "For personal interest or self-development");

	private String displayName;
	private int value;

	private StudyReason(int value, String displayName) {
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
