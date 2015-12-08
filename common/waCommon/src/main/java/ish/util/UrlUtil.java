/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
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
	
	public static final String PORTAL_URL = "https://skillsoncourse.com.au/portal";
	
	public static final String EXPIRY_DATE_FORMAT = "yyyyMMdd";
	
	private static final String URL_PARAM_START = "?";
	private static final String URL_PARAM_DELIMITER = "&";
	private static final String URL_PART_DELIMITER = "/";
	
	private static final String USI_PART = "usi";
	public static final String VALID_UNTIL = "valid";
	public static final String KEY = "key";

    private static final Pattern PORTAL_LINK_PATTERN = Pattern.compile("https://(.*)/portal(.*)&key=");
	private static final Pattern GENERIC_LINK_PATTERN = Pattern.compile("(.*)&key=");

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
	 * @deprecated use {@link UrlUtil#signUrl(String, Date, String)} instead
	 */
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
	 * @deprecated use {@link UrlUtil#validateSignedUrl(String, String, Date)} instead
	 */
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
	 * Transforms given URL into signed URL by appending 'valid' and 'key' parameters.
	 *
	 * Complete URL will look like this:
	 *
	 * https://skillsoncourse.com.au/portal/usi/uniqueCode?valid=21140101&key=k9_S8uk68W5PoCvq5lSUp70sqQY
	 *
	 *
	 * @param url - unsigned URL
	 * @param expiry - URL expiry date
	 * @param hashSalt - salt for hashing
	 * @return - temporary signed URL
	 */
	public static String signUrl(String url, Date expiry, String hashSalt) {
		if (StringUtils.trimToNull(url) == null) {
			throw new IllegalArgumentException("URL cannnot be null.");
		}
		if (StringUtils.trimToNull(hashSalt) == null) {
			throw new IllegalArgumentException("Hash salt cannot be null.");
		}
		if (expiry == null) {
			throw new IllegalArgumentException("Expiry date cannot be null.");
		}

		DateFormat expiryDateFormat = new SimpleDateFormat(EXPIRY_DATE_FORMAT);

		StringBuilder urlBuilder = new StringBuilder(url);

		if (!url.contains(URL_PARAM_START)) {
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

		return urlBuilder.toString();
	}

	/**
	 * Checks validity of given signed URL.
	 *
	 * @param url - signed URL
	 * @param salt - salt used to verify hash
	 * @param validUntil - URL will be considered expired and therefore invalid its expiry date is
	 *                   before this date
	 * @return - true if URL is valid and not expired
	 */
	public static boolean validateSignedUrl(String url, String salt, Date validUntil) {
		Matcher linkMatcher = GENERIC_LINK_PATTERN.matcher(url);

		if (linkMatcher.find()) {
			String strippedLink = linkMatcher.group(1);

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
