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

package ish.validation;

import org.apache.commons.lang3.StringUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.net.MalformedURLException;
import java.net.URL;

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

	public static boolean isValidUrl(String url) {
		try {
			new URL(url);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}
}
