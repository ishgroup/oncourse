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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * The USI check character is calculated using a Luhn Mod N algorithm
 */
public class UsiUtil {

	private static final Logger logger = LogManager.getLogger();

	private static final char[] validChars = {'2', '3', '4', '5', '6', '7', '8', '9',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R',
			'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

	public static boolean validateKey(String key) {
		return key != null &&
				key.length() == 10 &&
				generateAndCheckCharacter(key);
	}

	// Implementation of Luhn Mod N algorithm for check digit.
	private static boolean generateAndCheckCharacter(String key) {
		String keyToUpper = key.substring(0,9);
		char lastChar = key.charAt(9);
		int factor = 2;
		int sum = 0;
		int length = validChars.length;

		// Starting from the right and working leftwards is easier since
		// the initial "factor" will always be "2"
		for (int i = keyToUpper.length() - 1; i>= 0; i--) {
			int codePoint = Arrays.binarySearch(validChars, keyToUpper.charAt(i));
			if (codePoint < 0) {
				logger.warn("invalid Char for USI: {}", keyToUpper.charAt(i));
				return false;
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

		//check last character with generated;
		return lastChar == validChars[checkCodePoint];
	}
}
