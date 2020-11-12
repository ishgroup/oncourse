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
package ish.util;

/**
 * Utility methods for credit cards
 */
public final class CreditCardUtil {

	private CreditCardUtil() {}

	/**
	 * replaces all but first 6 and last 3 digits with X
	 *
	 * @param ccnumber to obfuscate
	 * @return obfuscated CC number
	 */
	public static String obfuscateCCNumber(final String ccnumber) {
		if (ccnumber == null) {
			return null;
		}
		final StringBuilder buff = new StringBuilder(ccnumber);
		for (int i = 6, count = buff.length() - 3; i < count; i++) {
			buff.setCharAt(i, 'X');
		}
		return buff.toString();
	}

	/**
	 * replaces all digits with X
	 *
	 * @param cvv to obfuscate
	 * @return obfuscated cvv
	 */
	public static String obfuscateCVVNumber(final String cvvnumber) {
		return cvvnumber == null ? null : cvvnumber.replaceAll(".", "X");
	}
}
