/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * The USI check character is calculated using a Luhn Mod N algorithm
 */
public class UsiUtil {

	private static final Logger logger = LogManager.getLogger(UsiUtil.class);

	private static final char[] validChars = {'2', '3', '4', '5', '6', '7', '8', '9',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R',
			'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

	public static boolean validateKey(String key) {
		return key != null &&
				key.length() == 10 &&
				key.charAt(9) == generateCheckCharacter(key.toUpperCase().substring(0,9));
	}

	// Implementation of Luhn Mod N algorithm for check digit.
	private static Character generateCheckCharacter(String key) {
		int factor = 2;
		int sum = 0;
		int length = validChars.length;

		// Starting from the right and working leftwards is easier since
		// the initial "factor" will always be "2"
		for (int i = key.length() - 1; i>= 0; i--) {
			int codePoint = Arrays.binarySearch(validChars, key.charAt(i));
			if (codePoint < 0) {
				logger.warn(String.format("invalid Char for USI: %s", key.charAt(i)));
				return null;
			}
			int addend = factor * codePoint;

			// Alternate the "factor" that each "codePoint" is multiplied by
			factor = (factor == 2) ? 1 : 2;

			// Sum the digits of the "addend" as expressed in base "n"
			addend = (addend / length) + (addend % length);
			sum += addend;
		}

		// Calculate the number that must be added to the "sum"
		// to make it divisible by "n"

		int remainder = sum % length;
		int checkCodePoint = (length - remainder) % length;

		return validChars[checkCodePoint];
	}
}
