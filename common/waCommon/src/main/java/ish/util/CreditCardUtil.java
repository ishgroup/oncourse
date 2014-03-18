/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
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
