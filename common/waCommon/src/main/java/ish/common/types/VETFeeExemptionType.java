/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * A set of values for AVETMISS reporting from the version 7 standard.
 * Consult the AVETMISS documentation for more detail about these options.
 * 
 */
@API
public enum VETFeeExemptionType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 */
	@API
	UNSET(-1, "Not set"),

	/**
	 */
	@API
	NO(0, "No (N)"),

	/**
	 */
	@API
	YES(1, "Yes (Y)"),

	/**
	 */
	@API
	C(9, "QLD only: Concessional Participant (C)"),

	/**
	 */
	@API
	D(11, "WA only: Pensioner Concession Card (D)"),

	/**
	 */
	@API
	E(12, "WA only: Repatriation Health Benefits Cards (E)"),

	/**
	 */
	@API
	F(13, "WA only: Fee Exempt (F)"),

	/**
	 */
	@API
	G(2, "VIC: VCE Scholarship, WA: AUSTUDY/ABSTUDY (G)"),

	/**
	 */
	@API
	H(3, "VIC only: Health Care Card (H)"),

	/**
	 */
	@API
	I(14, "WA only: Health Care Card Youth Allowance - Job seeker (Fee Free) (I)"),

	/**
	 */
	@API
	J(15, "WA only: Job Network Card (Fee Free) (J)"),

	/**
	 */
	@API
	L(16, "WA only: Under 18 Years of Age (L)"),

	/**
	 */
	@API
	M(4, "VIC only: Prisoner (M)"),

	/**
	 */
	@API
	N(10, "QLD: Non-concessional participant, WA: Health Care Card (N)"),

	/**
	 */
	@API
	O(5, "VIC: Other, WA: Youth Allowance (O)"),

	/**
	 */
	@API
	P(6, "VIC only: Pensioner concession card (P)"),

	/**
	 */
	@API
	Q(17, "WA only: Custodial Institution Inmates (Prison Inmates) (Q)"),

	/**
	 */
	@API
	S(18, "WA only: Health Care Card - New Start (Fee Free) (S)"),

	/**
	 */
	@API
	V(7, "VIC: Veteran gold card concession, WA: Fees Waived (due to severe financial hardship) (V)"),

	/**
	 */
	@API
	Z(8, "VIC: None, WA: No Concession  (Z)");

	private final Integer value;
	private String displayName;

	private VETFeeExemptionType(Integer value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDatabaseValue()
	 */
	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
