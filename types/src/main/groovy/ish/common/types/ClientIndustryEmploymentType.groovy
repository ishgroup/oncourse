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
 * A set of values for AVETMISS reporting for Victoria only.
 * Consult the AVETMISS documentation for more detail about these options.
 * See http://www.education.vic.gov.au/training/employers/industry/Pages/marketinfo.aspx for reference.
 *
 */
@API
public enum ClientIndustryEmploymentType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 */
	@API
	NOT_SET(0, "Not Stated", ""),

	/**
	 * Database value: 1
	 */
	@API
	AGRICULTURE(1, "Agriculture, Forestry and Fishing (A)", "A"),

	/**
	 * Database value: 2
	 */
	@API
	MINING(2, "Mining (B)", "B"),

	/**
	 * Database value: 3
	 */
	@API
	MANUFACTURING(3, "Manufacturing (C)", "C"),

	/**
	 * Database value: 4
	 */
	@API
	ELECTRICITY(4, "Electricity, Gas, Water and Waste Services (D)", "D"),

	/**
	 * Database value: 5
	 */
	@API
	CONSTRUCTION(5, "Construction (E)", "E"),

	/**
	 * Database value: 6
	 */
	@API
	WHOLESALE(6, "Wholesale Trade (F)", "F"),

	/**
	 * Database value: 7
	 */
	@API
	RETAIL(7, "Retail Trade (G)", "G"),

	/**
	 * Database value: 8
	 */
	@API
	ACCOMODATION(8, "Accommodation and Food Services (H)", "H"),

	/**
	 * Database value: 9
	 */
	@API
	TRANSPORT(9, "Transport, Postal and Warehousing (I)", "I"),

	/**
	 * Database value: 10
	 */
	@API
	MEDIA(10, "Information Media and Telecommunications (J)", "J"),

	/**
	 * Database value: 11
	 */
	@API
	FINANCIAL(11, "Financial and Insurance Services (K)", "K"),

	/**
	 * Database value: 12
	 */
	@API
	RENTAL(12, "Rental, Hiring and Real Estate Services (L)", "L"),

	/**
	 * Database value: 13
	 */
	@API
	PROFESSIONAL(13, "Professional, Scientific and Technical Services - Administrative and Support Services (M)", "M"),

	/**
	 * Database value: 14
	 */
	@API
	ADMIN(14, "Public Administration and Safety (N)", "N"),

	/**
	 * Database value: 15
	 */
	@API
	EDUCATION(15, "Education and Training (O)", "O"),

	/**
	 * Database value: 16
	 */
	@API
	HEALTH(16, "Health Care and Social Assistance (P)", "P"),

	/**
	 * Database value: 17
	 */
	@API
	ARTS(17, "Arts and Recreation Services (Q)", "Q"),

	/**
	 * Database value: 18
	 */
	@API
	OTHER(18, "Other Services (R)", "R");


	private String displayName;
	private int value;
	private String code;

	private ClientIndustryEmploymentType(int value, String displayName, String code) {
		this.value = value;
		this.displayName = displayName;
		this.code = code;
	}

	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	public String getCode() {
		return this.code;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

}
