/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * Credit cards accepted by onCourse.
 * 
 */
@API
public enum CreditCardType implements DisplayableExtendedEnumeration<String> {
	// JCB("J", "JCB"),
	/**
	 * Database value: V
	 */
	@API
	VISA("V", "VISA"),

	/**
	 * Database value: M
	 */
	@API
	MASTERCARD("M", "Mastercard"),


	@Deprecated
	BANKCARD("B", "Bankcard"),

	/**
	 * Database value: A
	 */
	@API
	AMEX("A", "Amex");
	// CARTE_BLANCHE("CB", "Carte Blanche"),
	// DISCOVER_NOVUS("DN", "Discover Novus"),
	// CHINA_UNION_PAY("C", "China Union Pay");

	private String displayName;
	private String persistentValue;

	private CreditCardType(String persistentValue, String displayName) {
		this.persistentValue = persistentValue;
		this.displayName = displayName;
	}

	@Override
	public String getDatabaseValue() {
		return this.persistentValue;
	}

	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

}
