/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * A set of values for AVETMISS reporting for Victoria only.
 * Consult the AVETMISS documentation for more detail about these options.
 *
 * @PublicApi
 */
public enum ClientIndustryEmploymentType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	NOT_SET(0, "Not Stated", ""),

	/**
	 * @PublicApi
	 */
	AGRICULTURE(1, "Agriculture, Forestry and Fishing (A)", "A"),

	/**
	 * @PublicApi
	 */
	MINING(2, "Mining (B)", "B"),

	/**
	 * @PublicApi
	 */
	MANUFACTURING(3, "Manufacturing (C)", "C"),

	/**
	 * @PublicApi
	 */
	ELECTRICITY(4, "Electricity, Gas, Water and Waste Services (D)", "D"),

	/**
	 * @PublicApi
	 */
	CONSTRUCTION(5, "Construction (E)", "E"),

	/**
	 * @PublicApi
	 */
	WHOLESALE(6, "Wholesale Trade (F)", "F"),

	/**
	 * @PublicApi
	 */
	RETAIL(7, "Retail Trade (G)", "G"),

	/**
	 * @PublicApi
	 */
	ACCOMODATION(8, "Accommodation and Feed Services (H)", "H"),

	/**
	 * @PublicApi
	 */
	TRANSPORT(9, "Transport, Postal and Warehousing (I)", "I"),

	/**
	 * @PublicApi
	 */
	MEDIA(10, "Information Media and telecommunications (J)", "J"),

	/**
	 * @PublicApi
	 */
	FINANCIAL(11, "Financial and Insurance Services (K)", "K"),

	/**
	 * @PublicApi
	 */
	RENTAL(12, "Rental, Hiring and real Estate Services (L)", "L"),

	/**
	 * @PublicApi
	 */
	PROFESSIONAL(13, "Professional, Scientific and Technical Services - Administrative and Support Services (M)", "M"),

	/**
	 * @PublicApi
	 */
	ADMIN(14, "Public Administration and Safety (N)", "N"),

	/**
	 * @PublicApi
	 */
	EDUCATION(15, "Education and Training (O)", "O"),

	/**
	 * @PublicApi
	 */
	HEALTH(16, "Health Care and Social Assistance (P)", "P"),

	/**
	 * @PublicApi
	 */
	ARTS(17, "Arts and recreation Services (Q)", "Q"),

	/**
	 * @PublicApi
	 */
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
