/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * @author marcin
 */
public enum StudyReason implements DisplayableExtendedEnumeration<Integer> {

	STUDY_REASON_NOT_STATED(-1, "Not stated"),
	STUDY_REASON_JOB(0, "To get a job"),
	STUDY_REASON_DEVELOP_BUSINESS(1, "To develop my existing business"),
	STUDY_REASON_START_BUSINESS(2, "To start my own business"),
	STUDY_REASON_CAREER_CHANGE(4, "To try for a different career"),
	STUDY_REASON_BETTER_JOB(8, "To get a better job or promotion"),
	STUDY_REASON_JOB_REQUIREMENT(16, "It was a requirement of my job"),
	STUDY_REASON_EXTRA_JOB_SKILLS(32, "I wanted extra skills for my job"),
	STUDY_REASON_FOR_ANOTHER_COURSE(64, "To get into another course of study"),
	STUDY_REASON_OTHER(128, "Other reasons"),
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
