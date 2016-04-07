/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.validation;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class ValidationUtil {

	/**
	 * Validate the form of an email address.
	 */
	public static boolean isValidEmailAddress(String anEmailAddress) {
		if (anEmailAddress == null) {
			return false;
		}

		boolean result = true;

		try {
			new InternetAddress(anEmailAddress);
		} catch (AddressException ex) {
			result = false;
		}

		// since the above does not throw an exception for local email addresses
		// of the form "username" we need to test for them separately.
		String[] tokens = anEmailAddress.split("@");

		if (tokens.length != 2 || tokens[0].length() == 0 || tokens[1].length() == 0) {
			result = false;
		}

		return result;
	}

}
