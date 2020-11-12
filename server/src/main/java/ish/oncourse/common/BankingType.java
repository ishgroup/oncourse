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
package ish.oncourse.common;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * onCourse tracks the banking of payments in order to provide a proper audit trail of the cash flow
 * into and out of the business. Different types of payments are grouped together for the purpose
 * of banking and reconciliation against bank statements.
 */
@API
public enum BankingType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Banking performed manually, usually in response to a physical visit to a bank
	 * to deposit cheques and cash.
	 */
	@API
	MANUAL(1, "Manual"),

	/**
	 * Banking performed by onCourse in an automated fashion.
	 */
	@Deprecated
	GATEWAY(2, "Gateway"),

	@API
	AUTO_MCVISA(3, "MasterCard/VISA"),

	@API
	AUTO_AMEX(4, "AMEX"),

	@API
	AUTO_OTHER(5, "Other");

	private int value;
	private String displayName;

	BankingType(int value, String displayName) {
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
