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

import java.util.Locale;

public final class RuntimeUtil {

	public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
	public static final String HTML_LINE_SEPARATOR = "<br/>";
	
	/**
	 * Makes certain that a default locale is set for the system. If none is set then defaults to en-AU.
	 */
	public static void assertDefaultLocale() {
		Locale defaultLocale;

		defaultLocale = Locale.getDefault();
		if (defaultLocale == null || defaultLocale.getCountry() == null || defaultLocale.getCountry().length() != 2) {
			defaultLocale = new Locale("en", "AU");
			Locale.setDefault(defaultLocale);
		}
	}
	
	/**
	 * default constructor
	 */
	private RuntimeUtil() {}

}
