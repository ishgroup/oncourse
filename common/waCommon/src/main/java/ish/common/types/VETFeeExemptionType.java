/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

public enum VETFeeExemptionType implements DisplayableExtendedEnumeration<Integer> {

	UNSET(-1, "Not set"),
	NO(0, "No (N)"),
	YES(1, "Yes (Y)"),
	C(9, "QLD only: Concessional Participant (C)"),
	D(11, "WA only: Pensioner Concession Card (D)"),
	E(12, "WA only: Repatriation Health Benefits Cards (E)"),
	F(13, "WA only: Fee Exempt (F)"),
	G(2, "VIC: VCE Scholarship, WA: AUSTUDY/ABSTUDY (G)"),
	H(3, "VIC only: Health Care Card (H)"),
	I(14, "WA only: Health Care Card Youth Allowance - Job seeker (Fee Free) (I)"),
	J(15, "WA only: Job Network Card (Fee Free) (J)"),
	L(16, "WA only: Under 18 Years of Age (L)"),
	M(4, "VIC only: Prisoner (M)"),
	N(10, "QLD: Non-concessional participant, WA: Health Care Card (N)"),
	O(5, "VIC: Other, WA: Youth Allowance (O)"),
	P(6, "VIC only: Pensioner concession card (P)"),
	Q(17, "WA only: Custodial Institution Inmates (Prison Inmates) (Q)"),
	S(18, "WA only: Health Care Card - New Start (Fee Free) (S)"),
	V(7, "VIC: Veteran gold card concession, WA: Fees Waived (due to severe financial hardship) (V)"),
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
