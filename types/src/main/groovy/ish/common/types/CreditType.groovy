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
 * Details of prior study for which credit/RPL was offered
 * http://heimshelp.education.gov.au/sites/heimshelp/2015_data_requirements/2015dataelements/pages/561
 *
 */
@API
public enum CreditType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 */
	@API
	NO_CREDIT_RPL_WAS_OFFERED(0, "No credit/RPL was offered", "No credit/RPL was offered"),

	/**
	 * Database value: 100
	 */
	@API
	CREDIT_RPL_FOR_PRIOR_HIGHER_EDUCATION_STUDY_ONLY(100,
		"Credit/RPL was offered for prior higher education study only",
			"Credit/RPL was offered for prior higher education study only"),

	/**
	 * Database value: 200
	 */
	@API
	CREDIT_RPL_FOR_PRIOR_VET_STUDY_ONLY(200, "Credit/RPL was offered for prior VET study only",
		"Credit/RPL was offered for prior VET study only"),

	/**
	 * Database value: 300
	 */
	@API
	CREDIT_RPL_FOR_COMBINATION_OF_PRIOR_HIGHER_EDUCATION_AND_VET_STUDY(300,
		"Credit/RPL was offered for a combination of prior higher education and VET study",
			"Credit/RPL was offered for a combination of prior higher education and VET study"),

	/**
	 * Database value: 400
	 */
	@API
	CREDIT_RPL_FOR_STUDY_OUTSIDE_AUSTRALIA(400,
		"Credit/RPL was offered for study undertaken at an education provider outside Australia",
			"Credit/RPL was offered for study undertaken at an education provider outside Australia"),

	/**
	 * Database value: 500
	 */
	@API
	CREDIT_RPL_FOR_WORK_EXPERIENCE(500,
		"Credit/RPL was offered for work experience undertaken inside or outside Australia",
			"Credit/RPL was offered for work experience undertaken inside or outside Australia"),

	/**
	 * Database value: 600
	 */
	@API
	OTHER(600, "Other", "Other");

	private String displayValue;
	private String description;
	private int value;

	private CreditType(int value, String displayValue, String description) {
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
