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
	public static final String FILE_SEPARATOR = System.getProperty("file.separator", "/");
	public static final String HTML_LINE_SEPARATOR = "<br/>";

	public static final String OS_NAME = System.getProperty("os.name", "");

	/**
	 * @param o - the object to print to the console prefixed by "-- "
	 */
	public static void println(final Object o) {
		System.out.println("-- " + o);
	}

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
	 * @return true if runnning on linux
	 */
	public static boolean isRunningOnLinux() {
		return OS_NAME.toLowerCase().startsWith("linux");
	}

	/**
	 * @return true if running on os x
	 */
	public static boolean isRunningOnMac() {
		return OS_NAME.toLowerCase().startsWith("mac");
	}

	/**
	 * @return true if running on windows
	 */
	public static boolean isRunningOnWindows() {
		return OS_NAME.toLowerCase().startsWith("windows");
	}

	public static boolean isRunningOnJava8orLess() {
		return System.getProperty("java.version").startsWith("1.");
	}

	/**
	 * default constructor
	 */
	private RuntimeUtil() {}

}
