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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtil {

	public static final String PORTAL_URL = "http://www.skillsoncourse.com.au/portal";

	public static final String EXPIRY_DATE_FORMAT = "yyyyMMdd";

	private static final String URL_PARAM_START = "?";
	private static final String URL_PARAM_DELIMITER = "&";
	private static final String URL_PART_DELIMITER = "/";

	private static final String USI_PART = "usi";
	public static final String VALID_UNTIL = "valid";
	public static final String KEY = "key";

    private static final Pattern PORTAL_LINK_PATTERN = Pattern.compile("https://(.*)/portal(.*)&key=");

	/**
	 * Creates link to portal's USI details entry page and signs it with hash.
	 *
	 * Complete URL will look like this:
	 *
	 * https://skillsoncourse.com.au/portal/usi/uniqueCode?valid=21140101&key=k9_S8uk68W5PoCvq5lSUp70sqQY
	 *
	 * The URL part participating in hash is: /usi/uniqueCode?valid=21140101, which includes contact's unique code
	 * and URL expiry date.
	 *
	 * @param contactCode - contact's unique code
	 * @param expiry - URL expiry date
	 * @param hashSalt - salt for hashing
	 * @return - temporary URL to portal USI details collection page
	 * @deprecated use {@link UrlUtil#createSignedPortalUrl(String, Date, String)} instead
	 */
	@Deprecated
	public static String createPortalUsiLink(String contactCode, Date expiry, String hashSalt) {

		if (StringUtils.trimToNull(contactCode) == null) {
			throw new IllegalArgumentException("Contact unique code cannot be null.");
		}
		if (StringUtils.trimToNull(hashSalt) == null) {
			throw new IllegalArgumentException("Hash salt cannot be null.");
		}
		if (expiry == null) {
			throw new IllegalArgumentException("Expiry date cannot be null.");
		}

		DateFormat expiryDateFormat = new SimpleDateFormat(EXPIRY_DATE_FORMAT);

		StringBuilder urlBuilder = new StringBuilder();

		urlBuilder.append(URL_PART_DELIMITER);
		urlBuilder.append(USI_PART);
		urlBuilder.append(URL_PART_DELIMITER);
		urlBuilder.append(contactCode);
		urlBuilder.append(URL_PARAM_START);
		urlBuilder.append(VALID_UNTIL).append('=');
		urlBuilder.append(expiryDateFormat.format(expiry));

		String unsignedUrl = urlBuilder.toString();

		String hashKey = sha1Base64(hashSalt + unsignedUrl);

		urlBuilder.append(URL_PARAM_DELIMITER);
		urlBuilder.append(KEY).append('=');
		urlBuilder.append(hashKey);

		return PORTAL_URL + urlBuilder.toString();
	}

	/**
	 * Checks validity of given portal USI details collection page link.
	 *
	 * @param link - portal USI page link
	 * @param salt - salt used to verify hash
	 * @param validUntil - URL will be considered expired and therefore invalid its expiry date is
	 *                   before this date
	 * @return - true if URL is valid and not expired
	 * @deprecated use {@link UrlUtil#validateSignedPortalUrl(String, String, Date)} instead
	 */
	@Deprecated
	public static boolean validatePortalUsiLink(String link, String salt, Date validUntil) {
		Matcher linkMatcher = PORTAL_LINK_PATTERN.matcher(link);

		if (linkMatcher.find()) {
			String strippedLink = linkMatcher.group(2);

			Pattern expiryPattern = Pattern.compile("valid=(.*)");
			Matcher expiryMatcher = expiryPattern.matcher(strippedLink);

			if (expiryMatcher.find()) {
				DateFormat expiryDateFormat = new SimpleDateFormat(EXPIRY_DATE_FORMAT);

				String dateStr = expiryMatcher.group(1);

				Date expiryDate;

				try {
					expiryDate = expiryDateFormat.parse(dateStr);
				} catch (ParseException e) {
					return false;
				}

				if (DateUtils.truncatedCompareTo(validUntil, expiryDate, Calendar.DAY_OF_MONTH) < 0) {
					Pattern signPattern = Pattern.compile("&key=(.*)");
					Matcher signMatcher = signPattern.matcher(link);

					if (signMatcher.find()) {
						String signature = signMatcher.group(1);

						return signature.equals(sha1Base64(salt + strippedLink));
					}
				}
			}
		}

		return false;
	}

	/**
	 * Transforms given path into signed portal URL by prepending 'https://www.skillsoncourse.com.au/portal'
	 * and appending 'valid' and 'key' parameters.
	 *
	 * Complete URL will look like this:
	 *
	 * https://www.skillsoncourse.com.au/portal/usi/uniqueCode?valid=21140101&key=k9_S8uk68W5PoCvq5lSUp70sqQY
	 *
	 *
	 * @param path - portal page path
	 * @param expiry - URL expiry date
	 * @param hashSalt - salt for hashing
	 * @return - temporary signed URL
	 */
	public static String createSignedPortalUrl(String path, Date expiry, String hashSalt) {
		if (StringUtils.trimToNull(path) == null) {
			throw new IllegalArgumentException("Path cannot be null.");
		}
		if (StringUtils.trimToNull(hashSalt) == null) {
			throw new IllegalArgumentException("Hash salt cannot be null.");
		}
		if (expiry == null) {
			throw new IllegalArgumentException("Expiry date cannot be null.");
		}

		DateFormat expiryDateFormat = new SimpleDateFormat(EXPIRY_DATE_FORMAT);

		StringBuilder urlBuilder = new StringBuilder();

		if (!path.startsWith(URL_PART_DELIMITER)) {
			urlBuilder.append(URL_PART_DELIMITER);
		}
		urlBuilder.append(path);
		if (!path.contains(URL_PARAM_START)) {
			urlBuilder.append(URL_PARAM_START);
		} else {
			urlBuilder.append(URL_PARAM_DELIMITER);
		}
		urlBuilder.append(VALID_UNTIL).append('=');
		urlBuilder.append(expiryDateFormat.format(expiry));

		String unsignedUrl = urlBuilder.toString();

		String hashKey = sha1Base64(hashSalt + unsignedUrl);

		urlBuilder.append(URL_PARAM_DELIMITER);
		urlBuilder.append(KEY).append('=');
		urlBuilder.append(hashKey);

		return PORTAL_URL + urlBuilder.toString();
	}

	/**
	 * Checks validity of given signed portal URL.
	 *
	 * @param url - signed URL
	 * @param salt - salt used to verify hash
	 * @param validUntil - URL will be considered expired and therefore invalid its expiry date is
	 *                   before this date
	 * @return - true if URL is valid and not expired
	 */
	public static boolean validateSignedPortalUrl(String url, String salt, Date validUntil) {
		Matcher linkMatcher = PORTAL_LINK_PATTERN.matcher(url);

		if (linkMatcher.find()) {
			String strippedLink = linkMatcher.group(2);

			Pattern expiryPattern = Pattern.compile("valid=(.*)");
			Matcher expiryMatcher = expiryPattern.matcher(strippedLink);

			if (expiryMatcher.find()) {
				DateFormat expiryDateFormat = new SimpleDateFormat(EXPIRY_DATE_FORMAT);

				String dateStr = expiryMatcher.group(1);

				Date expiryDate;

				try {
					expiryDate = expiryDateFormat.parse(dateStr);
				} catch (ParseException e) {
					return false;
				}

				if (DateUtils.truncatedCompareTo(validUntil, expiryDate, Calendar.DAY_OF_MONTH) < 0) {
					Pattern signPattern = Pattern.compile("&key=(.*)");
					Matcher signMatcher = signPattern.matcher(url);

					if (signMatcher.find()) {
						String signature = signMatcher.group(1);

						return signature.equals(sha1Base64(salt + strippedLink));
					}
				}
			}
		}

		return false;
	}

	private static String sha1Base64(String str) {
		byte[] hash = DigestUtils.sha1(str);
		return Base64.encodeBase64URLSafeString(hash);
	}
}
