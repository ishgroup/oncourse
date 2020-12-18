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

package ish.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

/**
 * Old password hashing and verification.
 *
 * TODO: Remove after 1/4/2021
 */
@Deprecated
public class PasswordUtil {

	/**
	 * Validates an older style hash password storage.
	 **
	 * @param   password        password to check
	 * @param   correctHash     hash of the valid password
	 * @return                  true if the password is correct, false if not
	 */
	public static boolean validateOldPassword(String password, String correctHash)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		// Decode the hash into its parameters
		String[] params = correctHash.split(":");
		int iterations = Integer.parseInt(params[1]);
		byte[] salt = fromHex(params[2]);
		byte[] hash = fromHex(params[3]);

		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, hash.length * 8);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] testHash = skf.generateSecret(spec).getEncoded();
		return Arrays.equals(hash, testHash);
	}

	/**
	 * Converts a string of hexadecimal characters into a byte array.
	 *
	 * @param   hex         hex string
	 * @return              hex string decoded into a byte array
	 */
	private static byte[] fromHex(String hex) {
		byte[] binary = new byte[hex.length() / 2];

		for(int i = 0; i < binary.length; i++) {
			binary[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}

		return binary;
	}

}
