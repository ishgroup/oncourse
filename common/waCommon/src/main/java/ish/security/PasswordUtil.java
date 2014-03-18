package ish.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * Utility class gathering method performing password hashing and verification.
 *
 * See https://crackstation.net/hashing-security.htm
 */
public class PasswordUtil {

	private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

	private static final int SALT_SIZE = 24;
	private static final int HASH_SIZE = 24;
	private static final int ITERATIONS = 1000;

	private static final int ITERATION_INDEX = 1;
	private static final int SALT_INDEX = 2;
	private static final int PBKDF2_INDEX = 3;

	private static final String HASH_FORMAT = "%s:%d:%s:%s";

	/**
	 * Generates hash string in following format: algorithm:numberOfIterations:salt:hash
	 */
	public static String computeHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		// Generate a random salt
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[SALT_SIZE];
		random.nextBytes(salt);

		// Hash the password
		byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, HASH_SIZE);

		return String.format(HASH_FORMAT, ALGORITHM, ITERATIONS, toHex(salt), toHex(hash));
	}

	/**
	 * Validates a password using a hash.
	 *
	 * @param   password        password to check
	 * @param   correctHash     hash of the valid password
	 * @return                  true if the password is correct, false if not
	 */
	public static boolean validatePassword(String password, String correctHash)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		// Decode the hash into its parameters
		String[] params = correctHash.split(":");
		int iterations = Integer.parseInt(params[ITERATION_INDEX]);
		byte[] salt = fromHex(params[SALT_INDEX]);
		byte[] hash = fromHex(params[PBKDF2_INDEX]);

		byte[] testHash = pbkdf2(password.toCharArray(), salt, iterations, hash.length);

		return slowEquals(hash, testHash);
	}

	/**
	 *  Computes the PBKDF2 hash of a password.
	 *
	 * @param   password    password to hash.
	 * @param   salt        salt
	 * @param   iterations  iteration count (slowness factor)
	 * @param   bytes       length of the hash to compute in bytes
	 * @return              PBDKF2 hash of the password
	 */
	public static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
		SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
		return skf.generateSecret(spec).getEncoded();
	}

	/**
	 * Converts a byte array into a hexadecimal string.
	 *
	 * @param   array       byte array to convert
	 * @return              a (length * 2) character string encoding the byte array
	 */
	private static String toHex(byte[] array) {
		BigInteger bi = new BigInteger(1, array);
		String hex = bi.toString(16);

		int paddingLength = (array.length * 2) - hex.length();

		if(paddingLength > 0) {
			return String.format("%0" + paddingLength + "d", 0) + hex;
		} else {
			return hex;
		}
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

	/**
	 * Compares two byte arrays in length-constant time. This comparison method
	 * is used so that password hashes cannot be extracted from an on-line
	 * system using a timing attack and then attacked off-line.
	 *
	 * @param   a       the first byte array
	 * @param   b       the second byte array
	 * @return          true if both byte arrays are the same, false if not
	 */
	private static boolean slowEquals(byte[] a, byte[] b) {
		int diff = a.length ^ b.length;
		for(int i = 0; i < a.length && i < b.length; i++) {
			diff |= a[i] ^ b[i];
		}

		return diff == 0;
	}
}
