package ish.oncourse.utils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * adapted ISHPhoneValidator:
 * 
 * Validation utility class containing common phone number validation routines.
 * <p>
 * Copyright 2005 ish group. All rights reserved.
 * </p>
 * 
 * 
 * @author Marek Wawrzyczny
 * @version 1.0
 * @since 1.0
 */
public class PhoneValidator {

	// FIXME: verify logic
	private static List<String> _VALID_AREA_CODES = Arrays.asList("02", "03", "04", "07", "08");

	public static String stripNonNumericChars(String value) {
		if (value != null) {
			String newValue = value.trim().replaceAll("\\s+", "");
			Pattern p = Pattern.compile("(\\d{8,})");
			Matcher m = p.matcher(newValue);
			while (m.find()) {
				return m.group(1);
			}
		}
		return null;
	}

	/**
	 * Validates a standard australian mobile phone number.
	 * <p>
	 * Requires that the number:
	 * <ul>
	 * <li>contains digits only</li>
	 * <li>is 10 digit long</li>
	 * <li>starts with 02, 03, 04, 07, 08 (note that Hutchison released mobile
	 * numbers that look like land-line numbers)</li>
	 * </ul>
	 * 
	 * @param pValue
	 *            the value to validate
	 * @throws validation
	 *             exception with refs to object and key
	 * @return a <b>formatted</b> value if value is valid
	 */
	public static String validateMobileNumber(String pValue) throws Exception

	{
		String message = null;
		String digitOnlyString = StringUtilities.stripAlphas(pValue);
		if (!digitOnlyString.matches("[\\([0-9]\\-\\.\\ \\:\\)]+")) {
			message = "The mobile number must contain digits only.";
		} else if (digitOnlyString.length() != 10) {
			message = "The mobile number must be 10 digits starting with 04.";
		} else if (!"04".equals(digitOnlyString.substring(0, 2))) {
			message = "The mobile number must be 10 digits starting with 04.";
		}
		if (message != null) {
			throw new Exception(message);
		}
		return digitOnlyString;
	}

	/**
	 * Validates a standard australian phone number.
	 * <p>
	 * Requires that the number:
	 * <ul>
	 * <li>contains digits only</li>
	 * <li>is 10 digit long</li>
	 * <li>starts with 02, 03, 04, 07, 08</li>
	 * </ul>
	 * 
	 * @param pValue
	 *            the value to validate
	 * @throws validation
	 *             exception with refs to object and key
	 * @return a <b>formatted</b> value if value is valid
	 */
	public static String validatePhoneNumber(String pValue) throws Exception {
		String message;
		String digitOnlyString;

		message = null;
		digitOnlyString = StringUtilities.stripAlphas(pValue);
		if (digitOnlyString == null || digitOnlyString.length() != 10) {
			message = "The phone number appears to be incomplete. Please ensure it has 10 digits (including the area code).";
		} else if (!_VALID_AREA_CODES.contains(digitOnlyString.substring(0, 2))) {
			message = "The phone number must start with either the area code or 04 for mobile numbers.";
		}

		if (message != null) {
			throw new Exception(message);
		}
		return digitOnlyString;
	}

	private PhoneValidator() {
	}

}
