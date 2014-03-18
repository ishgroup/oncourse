/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

/**
 * No comment at this time.
 */
public final class SecurityUtil {
	public static final int VOUCHER_CODE_LENGTH = 8;

	private static SecureRandom random = new SecureRandom();

	/*
	 * A list of human readable characters without ambiguous '1/l/I' and 'O/0'
	 */
	private static String humanReadableChars = "23456789qwertyuiopkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM";

	/*
	 * A list of legal for voucher code human readable characters without ambiguous '1/l/L/i/I' and 'O/0'
	 */
	private static String voucherCodeLegalChars = "23456789qwertyupkjhgfdsazxcvbnmQWERTYUPKJHGFDSAZXCVBNM";

	private SecurityUtil() {}

	/**
	 * Generates a random string.
	 * 
	 * @param length
	 * @return a random string
	 */
	public static String generateRandomPassword(int length) {
		StringBuilder result = new StringBuilder();
		while (result.length() < length) {
			int index = random.nextInt(humanReadableChars.length());
			result.append(humanReadableChars.charAt(index));
		}
		return result.toString();
	}

	public static String generateVoucherCode() {
		StringBuilder code = new StringBuilder();
		while (code.length() < VOUCHER_CODE_LENGTH) {
			int index = random.nextInt(voucherCodeLegalChars.length());
			code.append(voucherCodeLegalChars.charAt(index));
		}
		return code.toString();
	}

	/**
	 * Take a clear text password and return a hash. If an error occurs, null is returned.
	 * 
	 * @return hashed password
	 * @throws UnsupportedEncodingException
	 */
	public static String hashPassword(String pass) throws UnsupportedEncodingException {
		if (pass == null) {
			return null;
		}
		return hashByteArray(pass.getBytes("UTF-8"));

	}

	/**
	 * Take a file and return a hash. If an error occurs, null is returned.
	 * 
	 * @return hashed password
	 */
	public static String hashByteArray(byte[] bArray) {
		if (bArray == null) {
			return null;
		}

		try {
			MessageDigest md = MessageDigest.getInstance("SHA", "SUN");
			byte[] hash = md.digest(bArray);
			StringBuilder result = new StringBuilder();
			for (byte aHash : hash) {
				// the mask 0xFF is used to ensure we don't get a signed integer (which then adds 2^32 to the hex value)
				result.append(Integer.toHexString(aHash & 0xFF));
			}
			return result.toString();
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (NoSuchProviderException e) {
			return null;
		}
	}
}
