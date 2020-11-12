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
package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API

/**
 * A set of values for AVETMISS reporting from the version 7 standard.
 * Consult the AVETMISS documentation for more detail about these options.
 */
@API
enum StudyReason implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: -1
	 *
	 * Not stated
	 */
	@API
	STUDY_REASON_NOT_STATED(-1, 'Not stated'),

	/**
	 * Database value: 0
	 *
	 * To get a job
	 */
	@API
	STUDY_REASON_JOB(0, 'To get a job'),

	/**
	 * Database value: 1
	 *
	 * To develop existing business
	 */
	@API
	STUDY_REASON_DEVELOP_BUSINESS(1, 'To develop my existing business'),

	/**
	 * Database value: 2
	 *
	 * To start own business
	 */
	@API
	STUDY_REASON_START_BUSINESS(2, 'To start my own business'),

	/**
	 * Database value: 4
	 *
	 * To try for a different career
	 */
	@API
	STUDY_REASON_CAREER_CHANGE(4, 'To try for a different career'),

	/**
	 * Database value: 8
	 *
	 * To get a better job or promotion
	 */
	@API
	STUDY_REASON_BETTER_JOB(8, 'To get a better job or promotion'),

	/**
	 * Database value: 13
	 *
	 * To get skills for community/voluntary work
	 */
	@API
	STUDY_REASON_VOLUNTARY_WORK(13, 'To get skills for community/voluntary work'),

	/**
	 * Database value: 16
	 *
	 * Was a requirement of a job
	 */
	@API
	STUDY_REASON_JOB_REQUIREMENT(16, 'It was a requirement of my job'),

	/**
	 * Database value: 32
	 *
	 * To get extra skills for a job
	 */
	@API
	STUDY_REASON_EXTRA_JOB_SKILLS(32, 'I wanted extra skills for my job'),

	/**
	 * Database value: 64
	 *
	 * To get into another course of study
	 */
	@API
	STUDY_REASON_FOR_ANOTHER_COURSE(64, 'To get into another course of study'),

	/**
	 * Database value: 128
	 *
	 * Other reasons
	 */
	@API
	STUDY_REASON_OTHER(128, 'Other reasons'),

	/**
	 * Database value: 256
	 *
	 * For personal interest or self-development
	 */
	@API
	STUDY_REASON_PERSONAL_INTEREST(256, 'For personal interest or self-development');

	private String displayName
	private int value

	private StudyReason(int value, String displayName) {
		this.value = value
		this.displayName = displayName
	}

	@Override
	Integer getDatabaseValue() {
		return this.value
	}

	@Override
	String getDisplayName() {
		return this.displayName
	}

	@Override
	String toString() {
		return getDisplayName()
	}

}
