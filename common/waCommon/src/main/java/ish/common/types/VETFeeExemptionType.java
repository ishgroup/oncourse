/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * @PublicApi
 */
public enum VETFeeExemptionType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	UNSET(-1, "Not set"),

	/**
	 * @PublicApi
	 */
	NO(0, "No (N)"),

	/**
	 * @PublicApi
	 */
	YES(1, "Yes (Y)"),

	/**
	 * @PublicApi
	 */
	C(9, "QLD only: Concessional Participant (C)"),

	/**
	 * @PublicApi
	 */
	D(11, "WA only: Pensioner Concession Card (D)"),

	/**
	 * @PublicApi
	 */
	E(12, "WA only: Repatriation Health Benefits Cards (E)"),

	/**
	 * @PublicApi
	 */
	F(13, "WA only: Fee Exempt (F)"),

	/**
	 * @PublicApi
	 */
	G(2, "VIC: VCE Scholarship, WA: AUSTUDY/ABSTUDY (G)"),

	/**
	 * @PublicApi
	 */
	H(3, "VIC only: Health Care Card (H)"),

	/**
	 * @PublicApi
	 */
	I(14, "WA only: Health Care Card Youth Allowance - Job seeker (Fee Free) (I)"),

	/**
	 * @PublicApi
	 */
	J(15, "WA only: Job Network Card (Fee Free) (J)"),

	/**
	 * @PublicApi
	 */
	L(16, "WA only: Under 18 Years of Age (L)"),

	/**
	 * @PublicApi
	 */
	M(4, "VIC only: Prisoner (M)"),

	/**
	 * @PublicApi
	 */
	N(10, "QLD: Non-concessional participant, WA: Health Care Card (N)"),

	/**
	 * @PublicApi
	 */
	O(5, "VIC: Other, WA: Youth Allowance (O)"),

	/**
	 * @PublicApi
	 */
	P(6, "VIC only: Pensioner concession card (P)"),

	/**
	 * @PublicApi
	 */
	Q(17, "WA only: Custodial Institution Inmates (Prison Inmates) (Q)"),

	/**
	 * @PublicApi
	 */
	S(18, "WA only: Health Care Card - New Start (Fee Free) (S)"),

	/**
	 * @PublicApi
	 */
	V(7, "VIC: Veteran gold card concession, WA: Fees Waived (due to severe financial hardship) (V)"),

	/**
	 * @PublicApi
	 */
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
