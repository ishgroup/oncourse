/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

public enum PaymentPlanDistribution implements DisplayableExtendedEnumeration<Integer> {
	
	MONTHLY(1, "Monthly"),
	WEEKLY(2, "Weekly"),
	FORTNIGHTLY(3, "Fortnightly");

	private String displayName;
	private int value;
	
	private PaymentPlanDistribution(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public Integer getDatabaseValue() {
		return value;
	}
}
