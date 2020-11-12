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

import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.StringUtils;

/**
 * Class serving util method for operations on strings
 *
 */
public class StringUtil {

	public static final char DEFAULT_PADDING_CHARACTER_NUMBER = '0';
	public static final char DEFAULT_PADDING_CHARACTER_STRING = ' ';
	public static final String COMMA_CHARACTER = ",";
	public static final String COLON = ":";

	/**
	 * default hidden constructor for this utility class
	 */
	private StringUtil() {}

    /**
	 * Returns a string of <code>len</code> spaces.
	 *
	 * @param len - number of spaces
	 * @return the resulting bytes.
	 */
	public static final String padField(int len) {
		return padField(len, "");
	}

	/**
	 * Pads data with spaces and returns the bytes from a string of length <code>len</code>.
	 *
	 * @param len - the total width of characters allowed/adjusted to
	 * @param data - the original data
	 * @return the resulting bytes.
	 */
	public static final String padField(int len, Object data) {
		if (data instanceof Number) {
			return padField(len, data, DEFAULT_PADDING_CHARACTER_NUMBER);
		}
		return padField(len, data, DEFAULT_PADDING_CHARACTER_STRING);
	}

	public static final String padFieldRight(int len, Object o) {
		if (o instanceof Number) {
			return padField(len, o.toString(), DEFAULT_PADDING_CHARACTER_NUMBER, true);
		}
		return padField(len, o.toString(), DEFAULT_PADDING_CHARACTER_STRING, true);
	}

	public static final String padFieldLeft(int len, Object o) {
		if (o instanceof Number) {
			return padField(len, o.toString(), DEFAULT_PADDING_CHARACTER_NUMBER, false);
		}
		return padField(len, o.toString(), DEFAULT_PADDING_CHARACTER_STRING, false);
	}

	/**
	 * Aligns data to the left, padding if necessary and returning the bytes from a string of length <code>len</code>.
	 *
	 * @return the resulting bytes.
	 * @param padCharacter The character which is used to pad strings smaller than the output length.
	 * @param len The total width of characters allowed/adjusted to
	 * @param o The original data
	 */
	public static final String padField(int len, Object o, char padCharacter) {
		if (o instanceof Number) {
			return padField(len, o.toString(), padCharacter, true);
		}
		return padField(len, o == null ? "" : o.toString(), padCharacter, false);
	}

	public static final String padFieldRight(int len, Object o, char padCharacter) {
		return padField(len, o.toString(), padCharacter, true);
	}

	public static final String padFieldLeft(int len, Object o, char padCharacter) {
		return padField(len, o.toString(), padCharacter, false);
	}

	public static final String padField(int len, Object o, char padCharacter, boolean alignRight) {
		return padField(len, o == null ? "" : o.toString(), padCharacter, alignRight);
	}

	/**
	 * Pads <code>data</code> to <code>len</code> characters aligning the original data as per <code>alignRight</code>.
	 *
	 * @param len - the total width of characters allowed/adjusted to
	 * @param padCharacter - the character to pad with
	 * @param data - the original data
	 * @param alignRight - whether to align the original data to the right or not
	 * @return the resulting bytes.
	 */
	public static final String padField(int len, String data, char padCharacter, boolean alignRight) {
		if (data == null) {
			return padField(len, "", padCharacter, alignRight);
		}

		String dataCorrected = data.replaceAll("[\\n\\r\\t]", "");
		int actualLength = dataCorrected.length();

		if (actualLength >= len) {
			// source is longer than output, just truncate
			return dataCorrected.substring(0, len);
		}

		StringBuilder result = new StringBuilder(dataCorrected);
		for (; actualLength < len; actualLength++) {
			if (alignRight) {
				result.insert(0, padCharacter);
			} else {
				result.append(padCharacter);
			}
		}
		return result.toString();
	}
	/**
	 * Normolize <code>unicodeString</code> to ASCII String
	 *
	 * @param unicodeString
	 * @return the resulting string - ASCII chars only.
	 */

	public static String normalizeString(String unicodeString) {
		String asciiString;
		//firstly replace all chars with different types of accents and underscores with simple chars from ASCII table
		asciiString = StringUtils.stripAccents(unicodeString);
		//then replace all non ASCII chars with a blank space
		asciiString = CharMatcher.ascii().negate().replaceFrom(asciiString, " ");
		return asciiString;
	}
}
