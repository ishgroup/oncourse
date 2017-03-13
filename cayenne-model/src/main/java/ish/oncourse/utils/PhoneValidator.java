package ish.oncourse.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

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
	 *             exception with refs to object and key
	 * @return a <b>formatted</b> value if value is valid
	 */
	@Deprecated //use MobileValidator
	public static String validateMobileNumber(String pValue) throws Exception
	{
		MobileValidator validator = MobileValidator.valueOf(pValue).validate();
		String message = validator.getMessage();
		if (message != null) {
			throw new Exception(message);
		}
		return validator.getValue();
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
	 *             exception with refs to object and key
	 * @return a <b>formatted</b> value if value is valid
	 */
	@Deprecated //use Validator
	public static String validatePhoneNumber(String phoneName, String pValue) throws Exception {
		Validator validator = Validator.valueOf(pValue, phoneName).validate();

		if (validator.getMessage() != null) {
			throw new Exception(validator.getMessage());
		}
		return validator.getValue();
	}

	private PhoneValidator() {
	}


	public static class MobileValidator {
		private String value;

		private String message;

		public MobileValidator validate() {
			value = StringUtils.trimToNull(value);
			if (value != null) {
				value = StringUtils.removePattern(value, "[^0-9]");
				if (!value.matches("[\\([0-9]\\-\\.\\ \\:\\)]+")) {
					message = "Enter 10 digit mobile phone number including 04 area code for Australian numbers";
				} else if (value.length() != 10) {
					message = "Enter 10 digit mobile phone number including 04 area code for Australian numbers";
				} else if (!"04".equals(value.substring(0, 2))) {
					message = "Enter 10 digit mobile phone number including 04 area code for Australian numbers";
				}
			}
			return this;
		}

		public String getMessage() {
			return message;
		}

		public String getValue() {
			return value;
		}

		public static  MobileValidator valueOf(String value) {
			MobileValidator validator = new MobileValidator();
			validator.value = value;
			return validator;
		}
	}

	public static class Validator {
		private String phoneName;
		private String value;

		private String message;

		public Validator validate() {
			value = StringUtils.trimToNull(value);
			if (value != null) {
				value  = StringUtils.removePattern(value, "[^0-9]");
				if (value.length() != 10) {
					message = String.format("Enter 10 digit %s phone number including area code for Australian numbers", phoneName);
				} else if (!_VALID_AREA_CODES.contains(value.substring(0, 2))) {
					message = String.format("Enter 10 digit %s phone number including area code for Australian numbers", phoneName);
				}
			}
			return this;
		}

		public String getMessage() {
			return message;
		}

		public String getPhoneName() {
			return phoneName;
		}

		public String getValue() {
			return value;
		}

		public static  Validator valueOf(String value, String phoneName) {
			Validator validator = new Validator();
			validator.value = value;
			validator.phoneName = phoneName;
			return validator;
		}

	}
}
