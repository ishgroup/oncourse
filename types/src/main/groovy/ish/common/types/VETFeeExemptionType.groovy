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
 * A set of values for AVETMISS reporting from the version 7 standard.
 * Consult the AVETMISS documentation for more detail about these options.
 *
 */
@API
public enum VETFeeExemptionType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: -1
	 *
	 * Not set
	 */
	@API
	UNSET(-1, "Not set"),

	/**
	 * Database value: 0
	 *
	 * No
	 */
	@API
	NO(0, "No (N)"),

	/**
	 * Database value: 1
	 *
	 * Yes
	 */
	@API
	YES(1, "Yes (Y)"),

	/**
	 * Database value: 9
	 *
	 * QLD only: Concessional Participant
	 */
	@API
	C(9, "QLD only: Concessional Participant (C)"),

	/**
	 * Database value: 11
	 *
	 * WA only: Pensioner Concession Card
	 */
	@API
	D(11, "WA only: Pensioner Concession Card (D)"),

	/**
	 * Database value: 12
	 *
	 * WA only: Repatriation Health Benefits Cards
	 */
	@API
	E(12, "WA only: Repatriation Health Benefits Cards (E)"),

	/**
	 * Database value: 13
	 *
	 * WA only: Fee Exempt
	 */
	@API
	F(13, "WA only: Fee Exempt (F)"),

	/**
	 * Database value: 2
	 *
	 * VIC: VCE Scholarship, WA: AUSTUDY/ABSTUDY
	 */
	@API
	G(2, "VIC: VCE Scholarship, WA: AUSTUDY/ABSTUDY (G)"),

	/**
	 * Database value: 3
	 *
	 * VIC only: Health Care Card
	 */
	@API
	H(3, "VIC only: Health Care Card (H)"),

	/**
	 * Database value: 14
	 *
	 * WA only: Health Care Card Youth Allowance - Job seeker (Fee Free)
	 */
	@API
	I(14, "WA only: Health Care Card Youth Allowance - Job seeker (Fee Free) (I)"),

	/**
	 * Database value: 15
	 *
	 * WA only: Job Network Card (Fee Free)
	 */
	@API
	J(15, "WA only: Job Network Card (Fee Free) (J)"),

	/**
	 * Database value: 16
	 *
	 * WA only: Under 18 Years of Age
	 */
	@API
	L(16, "WA only: Under 18 Years of Age (L)"),

	/**
	 * Database value: 4
	 *
	 * VIC only: Prisoner
	 */
	@API
	M(4, "VIC only: Prisoner (M)"),

	/**
	 * Database value: 10
	 *
	 * QLD: Non-concessional participant, WA: Health Care Card
	 */
	@API
	N(10, "QLD: Non-concessional participant, WA: Health Care Card (N)"),

	/**
	 * Database value: 5
	 *
	 * VIC: Other, WA: Youth Allowance
	 */
	@API
	O(5, "VIC: Other, WA: Youth Allowance (O)"),

	/**
	 * Database value: 6
	 *
	 * VIC only: Pensioner concession card
	 */
	@API
	P(6, "VIC only: Pensioner concession card (P)"),

	/**
	 * Database value: 17
	 *
	 * WA only: Custodial Institution Inmates (Prison Inmates)
	 */
	@API
	Q(17, "WA only: Custodial Institution Inmates (Prison Inmates) (Q)"),

	/**
	 * Database value: 18
	 *
	 * WA only: Health Care Card - New Start (Fee Free)
	 */
	@API
	S(18, "WA only: Health Care Card - New Start (Fee Free) (S)"),

	/**
	 * Database value: 7
	 *
	 * VIC: Veteran gold card concession, WA: Fees Waived (due to severe financial hardship)
	 */
	@API
	V(7, "VIC: Veteran gold card concession, WA: Fees Waived (due to severe financial hardship) (V)"),

	/**
	 * Database value: 8
	 *
	 * VIC: None, WA: No Concession
	 */
	@API
	Z(8, "VIC: None, WA: No Concession  (Z)"),

	/**
	 * Database value: 9
	 *
	 * Outreach officer/officers engagement
	 */
	@API
	OS(19, "ACE activity supported by the Outreach Officer (OS)");

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
