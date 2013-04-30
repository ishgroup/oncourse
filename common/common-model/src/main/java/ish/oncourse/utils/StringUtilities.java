package ish.oncourse.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * adapted ISHStringUtilities:
 * 
 * This class contains string conversion utilities.
 * 
 * @author Marek Wawrzyczny
 */
public class StringUtilities extends Object {

	/**
	 * @param string
	 *            might be "TRUE" or "true" or "YES" or "yes"
	 * @return true IFF string.lowerCase() == "yes" or "true"
	 */
	public static boolean booleanFromString(String string) {
		if (string == null) {
			return false;
		}
		string = string.toLowerCase();
		return "yes".equals(string) || "true".equals(string);
	}

	/**
	 * Test if string is digit character only.
	 * 
	 * @param string
	 *            String to test
	 * @return True if string contains only digit characters, false otherwise.
	 */
	public static boolean containsDigitsOnly(String string) {
		if (string == null) {
			return false;
		}

		return string.matches("[0-9]+");
	}

	/**
	 * Test if string contains no digit characters.
	 * 
	 * @param string
	 *            String to test
	 * @return True if string contains no digit characters, false if string
	 *         contains one or more digit characters.
	 */
	public static boolean containsNoDigits(String string) {
		if (string == null) {
			return true;
		}

		for (int i = 0; i < string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				return false;
			}
		}
		return true;
	}

/**
    	 * Converts characters not displayed in HTML to HTML encoded characters.
    	 * <p>
    	 * Current conversions:
    	 * <ul>
    	 * <li>spaces to:</li>
    	 * <ul>
    	 * <li>" " where for single space</li>
    	 * <li>"&nbsp" where consecutive spaces are encountered</li>
    	 * </ul>
    	 * <li>'"' to "&nbsp;"</li>
    	 * <li>'&' to "&amp;" iff not itself part in "&amp"</li>
    	 * <li>'\' to "&#39;"</li>
    	 * <li>'<' to "&lt;"</li>
    	 * <li>'>' to "&gt;"</li>
    	 * <li>'\n' to "<br>"</li>
    	 * <li> <code>(0xffff & c) > 126</code> to <code>"&#" + 
    	 *     (new Integer(0xffff & c)).toString() + ";"</code></li>
    	 * </ul>
    	 * </p>
    	 * 
    	 * @param string
    	 *            String to convert
    	 * @return The converted string.
    	 */
	public static String convertToHtmlSafe(String string) {
		if (string == null) {
			return new String("");
		}

		StringBuilder sb = new StringBuilder(string.length());
		// true if last char was blank
		boolean lastWasBlankChar = false;
		int len = string.length();
		char c;

		for (int i = 0; i < len; i++) {
			c = string.charAt(i);
			if (c == ' ') {
				// Blank gets extra work, this solves the problem you get if you
				// replace all blanks with &nbsp;, if you do that you lost word
				// breaking.
				if (lastWasBlankChar) {
					lastWasBlankChar = false;
					sb.append("&nbsp;");
				} else {
					lastWasBlankChar = true;
					sb.append(' ');
				}
			} else {
				lastWasBlankChar = false;
				// HTML Special Chars
				if (c == '"') {
					sb.append("&quot;");
				} else if (c == '&') {
					// Fix to ensure we don't escape &amp;
					if (i < len - 4) {
						if (string.charAt(i + 1) != 'a'
								&& string.charAt(i + 2) != 'm'
								&& string.charAt(i + 3) != 'p'
								&& string.charAt(i + 4) != ';') {
							sb.append("&amp;");
						} else {
							sb.append(c);
						}
					} else {
						sb.append(c);
					}
				} else if (c == '\'') {
					sb.append("&#39;");
				} else if (c == '<') {
					sb.append("&lt;");
				} else if (c == '>') {
					sb.append("&gt;");
				} else if (c == '\n') {
					// Handle Newline
					sb.append("<br />");
				} else {
					int ci = 0xffff & c;
					if (ci < 127) {
						// nothing special only 7 Bit
						sb.append(c);
					} else {
						// Not 7 Bit use the unicode system
						sb.append("&#");
						sb.append(new Integer(ci).toString());
						sb.append(';');
					}
				}
			}
		}
		return sb.toString();
	}

	public static String[] linesFromInputStream(InputStream in)
			throws IOException {
		List<String> lines = new ArrayList<>();
		if (in != null) {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		}
		return lines.toArray(new String[lines.size()]);
	}

	public static String[] linesFromString(String string) throws IOException {
		String[] lines = null;
		if (string != null && !"".equals(string)) {
			lines = linesFromInputStream(new ByteArrayInputStream(
					string.getBytes()));
		}
		return lines == null ? new String[0] : lines;
	}

	/**
	 * Strips alpa characters from string leaving only digits.
	 * 
	 * @param string
	 *            String to convert
	 * @return The converted string.
	 */
	public static String stripAlphas(String string) {
		StringBuilder result = new StringBuilder();

		if (string != null) {
			for (int i = 0, count = string.length(); i < count; i++) {
				if (Character.isDigit(string.charAt(i))) {
					result.append(string.charAt(i));
				}
			}
		}
		return result.toString();
	}

	private StringUtilities() {
	}
}
