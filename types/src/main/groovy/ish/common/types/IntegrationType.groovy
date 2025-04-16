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


public enum IntegrationType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 1
	 */
	@API
	MOODLE(1, "Moodle"),


	/**
	 * Database value: 2
	 */
	@API
	MAILCHIMP(2, "Mailchimp"),

	@API
	SURVEY_MONKEY(3, "SurveyMonkey"),

	@API
	ALCHEMER(4, "Alchemer"),

	@API
	XERO(5, "Xero"),

	@API
	MYOB(6, "MYOB"),

	@API
	CLOUD_ACCESS(7, "Cloud Assess"),

	@API
	CANVAS(8, "Canvas"),

	@API
	MICROPOWER(9, "Micropower"),

	@API
	USI_AGENCY(10, "USI Agency"),

	@API
	VET_STUDENT_LOANS(11, "VET Student Loans"),

	@API
	GOOGLE_CLASSROOM(12, "Google Classroom"),

	@API
	COASSEMBLE(13, "Coassemble"),

	@API
	TALENT_LMS(14, "TalentLMS"),

	@API
	LEARN_DASH(15, "LearnDash"),

	@API
	AMAZON_S3(16, "Amazon S3"),

	@API
	MISCROSOFT_AZURE(17, "Microsoft Azure"),

	@API
	KRONOS(19, "Kronos"),

	@API
	OKTA(20, "Okta"),


	private String displayName
	private int value

	private InvoiceType(int value, String displayName) {
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
