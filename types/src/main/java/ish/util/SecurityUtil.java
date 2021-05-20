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

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * No comment at this time.
 */
public final class SecurityUtil {
	public static final int VOUCHER_CODE_LENGTH = 8;
	public static final int USI_SOFTWARE_ID_LENGTH = 9;
	public static final int CERTIFICATE_CODE_LENGTH = 11;
	public static final int INVITATION_CODE_LENGTH = 32;
	public static final String CERTIFICATE_NAME_SPACE = "c";

	private static final SecureRandom random = new SecureRandom();

	/*
	 * A list of human readable characters without ambiguous '1/l/I' and 'O/0'
	 */
	private static final String humanReadableChars = "23456789qwertyuiopkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM";

	/*
	 * A list of legal for voucher code human readable characters without ambiguous '1/l/L/i/I' and 'O/0'
	 */
	private static final String voucherCodeLegalChars = "23456789qwertyupkjhgfdsazxcvbnmQWERTYUPKJHGFDSAZXCVBNM";

	private static final int[] numericChars = {1,2,3,4,5,6,7,8,9,0};

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

	public static String generateCertificateCode() {
		return  String.format("%s%s",CERTIFICATE_NAME_SPACE, (RandomStringUtils.random(CERTIFICATE_CODE_LENGTH, 0, voucherCodeLegalChars.length(), false, false, voucherCodeLegalChars.toCharArray(), random)));
	}

	public static String generateUserInvitationToken() {
		return RandomStringUtils.random(INVITATION_CODE_LENGTH,
				0,
				humanReadableChars.length(),
				false,
				false,
				humanReadableChars.toCharArray(),
				random);
	}

	public static String generateUSISoftwareId() {
		StringBuilder code = new StringBuilder();
		int summ = 0;
		while (code.length() < USI_SOFTWARE_ID_LENGTH) {
			int index = random.nextInt(numericChars.length);
			int digit = numericChars[index];
			summ += digit;
			code.append(numericChars[index]);
		}
		return code.toString() + summ % 10;
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
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			return null;
		}
	}
}
