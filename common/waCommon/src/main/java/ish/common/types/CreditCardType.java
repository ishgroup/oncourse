/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * an enumeration of available credit card types<br/>
 * <br/>
 * at the moment we are using securepay, which accepts (@see http://www.securepay.com.au/banks_cards_accepted.html)
 * <ul>
 * <li>JCB</li>
 * <li>VISA (and VISA debit)</li>
 * <li>MasterCard (and MasterCard debit)</li>
 * <li>Dinners</li>
 * <li>Amex</li>
 * </ul>
 * securepay does not accept:
 * <ul>
 * <li>Carte blanche</li>
 * <li>China union pay</li>
 * <li>Discover Novus</li>
 * </ul>
 * 
 * @PublicApi
 */
public enum CreditCardType implements DisplayableExtendedEnumeration<String> {
	// JCB("J", "JCB"),
	/**
	 * @PublicApi
	 */
	VISA("V", "VISA"),

	/**
	 * @PublicApi
	 */
	MASTERCARD("M", "Mastercard"),

	/**
	 * @PublicApi
	 */
	@Deprecated
	BANKCARD("B", "Bankcard"),
	// DINNERS("D", "Dinners Club"),

	/**
	 * @PublicApi
	 */
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
