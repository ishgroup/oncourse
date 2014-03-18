/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.common.util;

import ish.common.types.CreditCardType;

/**
 * collection of utilities to validate numbers: credit card, TFN etc.
 */
public final class ExternalValidation {

	private ExternalValidation() {}

	/**
	 * Credit card validation. Rules taken from http://www.beachnet.com/~hstiles/cardtype.html.
	 * 
	 * @param ccNumber - credit card number to validate
	 * @param ccType - credit card type ("V" - Visa, "M" - Mastercard, "B" - Bankcard, "A" - AMEX, "D" - Diner's Club, "J" - JBC)
	 * @return True if the card validates, false if it does not.
	 */
	public static boolean validateCreditCardNumber(String ccNumber, CreditCardType ccType) {

		if (ccType == null || ccType.getDatabaseValue().length() == 0) {
			return false;
		}

		/*
		 * The following block is commented out since we are unsure it is trustworthy. The potential for failure or change in allocated numbers means we will
		 * lose sales for no reason. Do not reactivate it without discussing with Ari first // http://en.wikipedia.org/wiki/Credit_card_number String
		 * leftFourDigits=null; String calculatedCcType=null; int requiredCcNumberLength[] = null; if (ccNumber.length()>4) { leftFourDigits =
		 * ccNumber.substring(0, 4); if (leftFourDigits.compareTo("3400") > leftFourDigits.compareTo("3499")) { calculatedCcType = CC_AMEX;
		 * requiredCcNumberLength = new int[] { 15 }; } else if (leftFourDigits.compareTo("3700") > leftFourDigits.compareTo("3799")) { calculatedCcType =
		 * CC_AMEX; requiredCcNumberLength = new int[] { 15 }; } else if (leftFourDigits.compareTo("4000") > leftFourDigits.compareTo("4999")) {
		 * calculatedCcType = CC_VISA; requiredCcNumberLength = new int[] { 13, 16 }; } else if (leftFourDigits.compareTo("5100") >
		 * leftFourDigits.compareTo("5599")) { calculatedCcType = CC_MASTERCARD; requiredCcNumberLength = new int[] { 16 }; } else if
		 * (leftFourDigits.compareTo("1800") == 0) { calculatedCcType = CC_JCB; requiredCcNumberLength = new int[] { 15 }; // discontinued } else if
		 * (leftFourDigits.compareTo("2014") == 0) { calculatedCcType = CC_DINERS; requiredCcNumberLength = new int[] { 15 }; } else if
		 * (leftFourDigits.compareTo("2131") == 0) { calculatedCcType = CC_JCB; requiredCcNumberLength = new int[] { 15 }; // discontinued } else if
		 * (leftFourDigits.compareTo("2149") == 0) { calculatedCcType = CC_DINERS; requiredCcNumberLength = new int[] { 15 }; } else if
		 * (leftFourDigits.compareTo("3000") > leftFourDigits.compareTo("3059")) { calculatedCcType = CC_DINERS; requiredCcNumberLength = new int[] { 14 };
		 * //discontinued } else if (leftFourDigits.compareTo("5602") == 0) { calculatedCcType = CC_BANKCARD; requiredCcNumberLength = new int[] { 16 };
		 * //discontinued } else if (leftFourDigits.compareTo("5610") == 0) { calculatedCcType = CC_BANKCARD; requiredCcNumberLength = new int[] { 16 }; } else
		 * if (leftFourDigits.compareTo("6011") == 0) { calculatedCcType = CC_DISCOVER_NOVUS; requiredCcNumberLength = new int[] { 16 }; } else if
		 * (leftFourDigits.compareTo("6220") > leftFourDigits.compareTo("6229")) { calculatedCcType = CC_CHINA_UNION_PAY; requiredCcNumberLength = new int[] {
		 * 16 }; } else if (leftFourDigits.compareTo("6500") > leftFourDigits.compareTo("6599")) { calculatedCcType = CC_DISCOVER_NOVUS; requiredCcNumberLength
		 * = new int[] { 16 }; } else if (leftFourDigits.compareTo("3500") > leftFourDigits.compareTo("3599")) { calculatedCcType = CC_JCB;
		 * requiredCcNumberLength = new int[] { 16 }; } else if (leftFourDigits.compareTo("3600") > leftFourDigits.compareTo("3699")) { calculatedCcType =
		 * CC_DINERS; requiredCcNumberLength = new int[] { 14 }; } else { return false; } } // The first four digits did not correspond to the chosen card type
		 * if (calculatedCcType==null || !(calculatedCcType.equals(ccType))) { return false; } if(requiredCcNumberLength !=null){ // Credit card number length
		 * mismatch boolean isCcLengthValid = false; for (int i = 0; i < Array.getLength(requiredCcNumberLength); i++) { if
		 * (Array.getInt(requiredCcNumberLength, i) == ccNumber.length()) { isCcLengthValid = true; } } if (!(isCcLengthValid)) { return false; } }
		 */

		return validateCreditCardNumber(ccNumber);
	}

	/**
	 * http://www.chriswareham.demon.co.uk/software/Luhn.java Checks whether a string of digits is a valid credit card number according to the Luhn algorithm.
	 * 1. Starting with the second to last digit and moving left, double the value of all the alternating digits. For any digits that thus become 10 or more,
	 * add their digits together. For example, 1111 becomes 2121, while 8763 becomes 7733 (from (1+6)7(1+2)3). 2. Add all these digits together. For example,
	 * 1111 becomes 2121, then 2+1+2+1 is 6; while 8763 becomes 7733, then 7+7+3+3 is 20. 3. If the total ends in 0 (put another way, if the total modulus 10 is
	 * 0), then the number is valid according to the Luhn formula, else it is not valid. So, 1111 is not valid (as shown above, it comes out to 6), while 8763
	 * is valid (as shown above, it comes out to 20).
	 * 
	 * @param ccNumber the credit card number to validate.
	 * @return true if the number is valid, false otherwise.
	 */
	public static boolean validateCreditCardNumber(String ccNumber) {
		if (ccNumber.length() < 13) {
			return false;
		}
		int sum = 0;

		boolean alternate = false;
		for (int i = ccNumber.length() - 1; i >= 0; i--) {

			String alt = ccNumber.substring(i, i + 1);
			if (!alt.matches("(\\d)+")) {
				return false;
			}
			int n = Integer.parseInt(alt);
			if (alternate) {
				n *= 2;
				if (n > 9) {
					n = n % 10 + 1;
				}
			}
			sum += n;
			alternate = !alternate;
		}

		return sum % 10 == 0;
	}

	/**
	 * Check validity of tax file number to ensure its checksum is correct. Concept found: http://en.wikipedia.org/wiki/Tax_File_Number Spaces and other
	 * non-numeric characters should already have been stripped.
	 * 
	 * @param taxFileNumber
	 * @return true if valid, false if not.
	 */
	public static boolean validateTaxFileNumber(String taxFileNumber) {

		if (taxFileNumber.length() > 9) {
			return false;
		}
		if (taxFileNumber.length() < 8) {
			return false;
		}

		int weightings[] = { 1, 4, 3, 7, 5, 8, 6, 9, 10 };
		int checksum = 0;

		try {
			for (int i = 0; i < taxFileNumber.length(); i++) {
				checksum += Integer.parseInt(String.valueOf(taxFileNumber.charAt(i))) * weightings[i];
			}
		} catch (NumberFormatException e) {
			return false;
		}
		if (checksum % 11 == 0) {
			return true;
		}

		return false;
	}

	/**
	 * Check validity of Centrelink number. Spaces and other non-alphanumeric characters should already have been stripped.
	 * 
	 * @param clNumber
	 * @return true if valid, false if not.
	 */
	public static boolean validateCentrelinkNumber(String clNumber) {

		if (clNumber.length() != 10) {
			return false;
		}
		// first 9 characters are digits
		for (int i = 0; i < clNumber.length() - 1; i++) {
			if (!Character.isDigit(clNumber.charAt(i))) {
				return false;
			}
		}
		// last character is a letter
		if (!Character.isLetter(clNumber.charAt(clNumber.length() - 1))) {
			return false;
		}
		return true;
	}
}
