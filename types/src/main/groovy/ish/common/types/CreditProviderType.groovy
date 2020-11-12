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
 * Type of provider where VET study was undertaken
 * http://heimshelp.education.gov.au/sites/heimshelp/2015_data_requirements/2015dataelements/pages/564
 *
 */
@API
public enum CreditProviderType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 */
	@API
	NO_CREDIT_RPL_WAS_OFFERED_FOR_VET(0, "No credit/RPL was offered for VET",
		"No credit/RPL was offered for VET"),

	/**
	 * Database value: 10
	 */
	@API
	UNIVERSITY(10, "University", "University"),

	/**
	 * Database value: 19
	 */
	@API
	OTHER_HIGHER_EDUCATION_PROVIDER(19, "Other Higher Education Provider",
		"Other Higher Education Provider"),

	/**
	 * Database value: 20
	 */
	@API
	TAFE(20, "TAFE", "TAFE"),

	/**
	 * Database value: 21
	 */
	@API
	SECONDARY_SCHOOLS_OR_COLLEGES(21, "Secondary Schools/Colleges",
		"Secondary Schools/Colleges"),

	/**
	 * Database value: 29
	 */
	@API
	OTHER_REGISTERED_TRAINING_ORGANIZATIONS(29, "Other Registered Training Organisations",
		"Other Registered Training Organisations"),

	/**
	 * Database value: 90
	 */
	@API
	NOT_ELSEWHERE_CATEGORIZED(90, "Not elsewhere categorised",
		"Not elsewhere categorised");

	private String displayValue;
	private String description;
	private int value;

	private CreditProviderType(int value, String displayValue, String description) {
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
