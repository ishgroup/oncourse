/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.validation;

import org.apache.commons.lang3.StringUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class ValidationUtil {

	/**
	 * Validate the form of an email address.
	 */
	public static boolean isValidEmailAddress(String anEmailAddress) {
		if (StringUtils.isBlank(anEmailAddress)) {
			return false;
		}

		try {
			InternetAddress internetAddress = new InternetAddress(anEmailAddress);
			internetAddress.validate();
		} catch (AddressException ex) {
			return false;
		}

		if (anEmailAddress.contains(",")) {
			return false;
		}
		
		// since the above does not throw an exception for local email addresses
		// of the form "username" we need to test for them separately.
		String[] tokens = anEmailAddress.split("@");

		return !(tokens.length != 2 || tokens[0].length() == 0 || tokens[1].length() == 0);
	}
}
