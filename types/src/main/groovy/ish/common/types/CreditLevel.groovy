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
 * Level of education of prior VET study for which credit/RPL was offered
 * http://heimshelp.education.gov.au/sites/heimshelp/2015_data_requirements/2015dataelements/pages/563
 *
 */
@API
public enum CreditLevel implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 */
	@API
	NO_CREDIT_RPL_WAS_OFFERED_FOR_VET(0, "No credit/RPL was offered for VET", "No credit/RPL was offered for VET"),

	/*Vocational graduate levels*/
	/**
	 * Database value: 1
	 */
	@API
	VOCATIONAL_GRADUATE_CERTIFICATE(1, "Vocational Graduate Certificate", "Vocational Graduate Certificate"),

	/**
	 * Database value: 2
	 */
	@API
	VOCATIONAL_GRADUATE_DIPLOMA(2, "Vocational Graduate Diploma", "Vocational Graduate Diploma"),

	/*Advanced diploma levels*/
	/**
	 * Database value: 411
	 */
	@API
	ADVANCED_DIPLOMA(411, "Advanced Diploma", "Advanced Diploma"),

	/**
	 * Database value: 412
	 */
	@API
	STATEMENT_OF_ATTEINMENT_AT_ADVANCED_DIPLOMA_LEVEL(412, "Statement of Attainment at Advanced Diploma level",
		"Statement of Attainment at Advanced Diploma level"),

	/**
	 * Database value: 415
	 */
	@API
	BRIDGING_AND_ENABLING_COURSE_AT_ADVANCED_DIPLOMA(415, "Bridging and Enabling Course at Advanced Diploma",
		"Bridging and Enabling Course at Advanced Diploma"),

	/*Diploma levels*/
	/**
	 * Database value: 421
	 */
	@API
	DIPLOMA(421, "Diploma", "Diploma"),

	/**
	 * Database value: 422
	 */
	@API
	STATEMENT_OF_ATTEINMENT_AT_DIPLOMA_LEVEL(422, "Statement of Attainment at Diploma level",
		"Statement of Attainment at Diploma level"),

	/**
	 * Database value: 423
	 */
	@API
	BRIDGING_AND_ENABLING_COURSE_AT_DIPLOMA(423, "Bridging and Enabling Course at Diploma",
		"Bridging and Enabling Course at Diploma"),

	/*Certificate III and IV level*/
	/**
	 * Database value: 511
	 */
	@API
	CERTIFICATE_4_LEVEL(511, "Certificate IV", "Certificate IV"),

	/**
	 * Database value: 512
	 */
	@API
	STATEMENT_OF_ATTEINMENT_AT_CERTIFICATE_4_LEVEL(512, "Statement of Attainment at Certificate IV level",
		"Statement of Attainment at Certificate IV level"),

	/**
	 * Database value: 513
	 */
	@API
	BRIDGING_AND_ENABLING_COURSE_AT_CERTIFICATE_4_LEVEL(513, "Bridging and Enabling Course at Certificate IV level",
		"Bridging and Enabling Course at Certificate IV level"),

	/**
	 * Database value: 514
	 */
	@API
	CERTIFICATE_3_LEVEL(514, "Certificate III", "Certificate III"),

	/**
	 * Database value: 515
	 */
	@API
	STATEMENT_OF_ATTEINMENT_AT_CERTIFICATE_3_LEVEL(515, "Statement of Attainment at Certificate III level",
		"Statement of Attainment at Certificate III level"),

	/**
	 * Database value: 516
	 */
	@API
	BRIDGING_AND_ENABLING_COURSE_AT_CERTIFICATE_3_LEVEL(516, "Bridging and Enabling Course at Certificate III level",
		"Bridging and Enabling Course at Certificate III level"),

	/*Certificate I and II level*/
	/**
	 * Database value: 521
	 */
	@API
	CERTIFICATE_2_LEVEL(521, "Certificate II", "Certificate II"),

	/**
	 * Database value: 522
	 */
	@API
	STATEMENT_OF_ATTEINMENT_AT_CERTIFICATE_2_LEVEL(522, "Statement of Attainment at Certificate II level",
		"Statement of Attainment at Certificate II level"),

	/**
	 * Database value: 523
	 */
	@API
	BRIDGING_AND_ENABLING_COURSE_AT_CERTIFICATE_2_LEVEL(523, "Bridging and Enabling Course at Certificate II level",
		"Bridging and Enabling Course at Certificate II level"),

	/**
	 * Database value: 524
	 */
	@API
	CERTIFICATE_1_LEVEL(524, "Certificate I", "Certificate I"),

	/**
	 * Database value: 525
	 */
	@API
	STATEMENT_OF_ATTEINMENT_AT_CERTIFICATE_1_LEVEL(525, "Statement of Attainment at Certificate I level",
		"Statement of Attainment at Certificate I level"),

	/*Other*/
	/**
	 * Database value: 999
	 */
	@API
	OTHER(999, "Other qualification", "Other qualification, not elsewhere classified");

	private String displayValue;
	private String description;
	private int value;

	private CreditLevel(int value, String displayValue, String description) {
		this.displayValue = displayValue;
		this.description = description;
		this.value = value;
	}

	@Override
	public String getDisplayName() {
		return displayValue;
	}

	@Override
	public Integer getDatabaseValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
