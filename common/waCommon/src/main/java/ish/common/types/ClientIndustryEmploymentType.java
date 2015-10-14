/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * A set of values for AVETMISS reporting for Victoria only.
 * Consult the AVETMISS documentation for more detail about these options.
 *
 */
@API
public enum ClientIndustryEmploymentType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 */
	@API
	NOT_SET(0, "Not Stated", ""),

	/**
	 */
	@API
	AGRICULTURE(1, "Agriculture, Forestry and Fishing (A)", "A"),

	/**
	 */
	@API
	MINING(2, "Mining (B)", "B"),

	/**
	 */
	@API
	MANUFACTURING(3, "Manufacturing (C)", "C"),

	/**
	 */
	@API
	ELECTRICITY(4, "Electricity, Gas, Water and Waste Services (D)", "D"),

	/**
	 */
	@API
	CONSTRUCTION(5, "Construction (E)", "E"),

	/**
	 */
	@API
	WHOLESALE(6, "Wholesale Trade (F)", "F"),

	/**
	 */
	@API
	RETAIL(7, "Retail Trade (G)", "G"),

	/**
	 */
	@API
	ACCOMODATION(8, "Accommodation and Feed Services (H)", "H"),

	/**
	 */
	@API
	TRANSPORT(9, "Transport, Postal and Warehousing (I)", "I"),

	/**
	 */
	@API
	MEDIA(10, "Information Media and telecommunications (J)", "J"),

	/**
	 */
	@API
	FINANCIAL(11, "Financial and Insurance Services (K)", "K"),

	/**
	 */
	@API
	RENTAL(12, "Rental, Hiring and real Estate Services (L)", "L"),

	/**
	 */
	@API
	PROFESSIONAL(13, "Professional, Scientific and Technical Services - Administrative and Support Services (M)", "M"),

	/**
	 */
	@API
	ADMIN(14, "Public Administration and Safety (N)", "N"),

	/**
	 */
	@API
	EDUCATION(15, "Education and Training (O)", "O"),

	/**
	 */
	@API
	HEALTH(16, "Health Care and Social Assistance (P)", "P"),

	/**
	 */
	@API
	ARTS(17, "Arts and recreation Services (Q)", "Q"),

	/**
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
