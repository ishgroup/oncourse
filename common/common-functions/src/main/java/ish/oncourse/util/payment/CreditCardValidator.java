/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.util.payment;

import ish.common.types.CreditCardType;
import ish.common.util.ExternalValidation;

import java.util.Calendar;

public class CreditCardValidator {


	public static String validateNumber (String ccNumber) {
		if (ccNumber == null || ccNumber.equals("")) {
			return "The credit card number cannot be blank.";
		}
		CreditCardType creditCardType = determineCreditCardType(ccNumber);
		if (!ExternalValidation.validateCreditCardNumber(ccNumber)
				|| (creditCardType != null && !ExternalValidation.validateCreditCardNumber(ccNumber, creditCardType))) {
			return "Invalid credit card number.";
		}
		return null;
	}


	public static boolean validCvv(String cvv) {
		return cvv != null && cvv.matches("\\d{1,4}");
	}

	public static boolean validCCExpiry(String month, String year) {
		if (month == null || month.equals("") || year == null || year.equals("")) {
			return false;
		}
		
		if (!month.matches("\\d{1,2}") || !year.matches("\\d{4}")) {
			return false;
		}
		
		int ccExpiryMonth = Integer.parseInt(month) - 1;
		int ccExpiryYear = Integer.parseInt(year);
		Calendar today = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, ccExpiryMonth);
		cal.set(Calendar.YEAR, ccExpiryYear);

		if (cal.getTime().before(today.getTime())) {
			return false;
		}
		return true;
	}

	public static CreditCardType determineCreditCardType(String ccNumber) {
		CreditCardParser cardParser = new CreditCardParser();
		return cardParser.parser(ccNumber);
	}
}
