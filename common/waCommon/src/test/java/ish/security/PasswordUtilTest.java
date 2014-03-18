/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.security;

import org.junit.Test;

import static org.junit.Assert.*;

public class PasswordUtilTest {

	private static final Pbkdf2TestVector[] TEST_VECTORS = new Pbkdf2TestVector[] {
			new Pbkdf2TestVector("password", "salt", 1, 20,
					new byte[] { (byte) 0x0c, (byte) 0x60, (byte) 0xc8, (byte) 0x0f, (byte) 0x96, (byte) 0x1f, (byte) 0x0e,
								 (byte) 0x71, (byte) 0xf3, (byte) 0xa9, (byte) 0xb5, (byte) 0x24, (byte) 0xaf, (byte) 0x60,
								 (byte) 0x12, (byte) 0x06, (byte) 0x2f, (byte) 0xe0, (byte) 0x37, (byte) 0xa6 }),
			new Pbkdf2TestVector("password", "salt", 2, 20,
					new byte[] { (byte) 0xea, (byte) 0x6c, (byte) 0x01, (byte) 0x4d, (byte) 0xc7, (byte) 0x2d, (byte) 0x6f,
								 (byte) 0x8c, (byte) 0xcd, (byte) 0x1e, (byte) 0xd9, (byte) 0x2a, (byte) 0xce, (byte) 0x1d,
								 (byte) 0x41, (byte) 0xf0, (byte) 0xd8, (byte) 0xde, (byte) 0x89, (byte) 0x57}),
			new Pbkdf2TestVector("password", "salt", 4096, 20,
					new byte[] { (byte) 0x4b, (byte) 0x00, (byte) 0x79, (byte) 0x01, (byte) 0xb7, (byte) 0x65, (byte) 0x48,
								 (byte) 0x9a, (byte) 0xbe, (byte) 0xad, (byte) 0x49, (byte) 0xd9, (byte) 0x26, (byte) 0xf7,
								 (byte) 0x21, (byte) 0xd0, (byte) 0x65, (byte) 0xa4, (byte) 0x29, (byte) 0xc1}),
			new Pbkdf2TestVector("password", "salt", 16777216, 20,
					new byte[] { (byte) 0xee, (byte) 0xfe, (byte) 0x3d, (byte) 0x61, (byte) 0xcd, (byte) 0x4d, (byte) 0xa4,
								 (byte) 0xe4, (byte) 0xe9, (byte) 0x94, (byte) 0x5b, (byte) 0x3d, (byte) 0x6b, (byte) 0xa2,
								 (byte) 0x15, (byte) 0x8c, (byte) 0x26, (byte) 0x34, (byte) 0xe9, (byte) 0x84}),
			new Pbkdf2TestVector("passwordPASSWORDpassword", "saltSALTsaltSALTsaltSALTsaltSALTsalt", 4096, 25,
					new byte[] { (byte) 0x3d, (byte) 0x2e, (byte) 0xec, (byte) 0x4f, (byte) 0xe4, (byte) 0x1c, (byte) 0x84,
								 (byte) 0x9b, (byte) 0x80, (byte) 0xc8, (byte) 0xd8, (byte) 0x36, (byte) 0x62, (byte) 0xc0,
								 (byte) 0xe4, (byte) 0x4a, (byte) 0x8b, (byte) 0x29, (byte) 0x1a, (byte) 0x96, (byte) 0x4c,
								 (byte) 0xf2, (byte) 0xf0, (byte) 0x70, (byte) 0x38}),
			new Pbkdf2TestVector("pass\0word", "sa\0lt", 4096, 16,
					new byte[] { (byte) 0x56, (byte) 0xfa, (byte) 0x6a, (byte) 0xa7, (byte) 0x55, (byte) 0x48, (byte) 0x09,
								 (byte) 0x9d, (byte) 0xcc, (byte) 0x37, (byte) 0xd7, (byte) 0xf0, (byte) 0x34, (byte) 0x25,
								 (byte) 0xe0, (byte) 0xc3})
	};

	@Test
	public void testPbkdf2() throws Exception {
		for (Pbkdf2TestVector vector : TEST_VECTORS) {
			byte[] dk = PasswordUtil.pbkdf2(vector.password.toCharArray(), vector.salt.getBytes(), vector.c, vector.dkLen);
			assertEquals(new String(vector.dk), new String(dk));
		}
	}

	@Test
	public void testHashPassword() throws Exception {
		String password = "password";
		String wrongPassword = "pAssword";

		String hash1 = PasswordUtil.computeHash(password);
		String hash2 = PasswordUtil.computeHash(password);

		assertNotEquals(hash1, hash2);

		assertTrue(PasswordUtil.validatePassword(password, hash1));
		assertTrue(PasswordUtil.validatePassword(password, hash2));

		assertFalse(PasswordUtil.validatePassword(wrongPassword, hash1));
		assertFalse(PasswordUtil.validatePassword(wrongPassword, hash2));
	}

	static class Pbkdf2TestVector {

		private String password;
		private String salt;
		private int c;
		private int dkLen;
		private byte[] dk;

		public Pbkdf2TestVector(String password, String salt, int c, int dkLen, byte[] dk) {
			this.password = password;
			this.salt = salt;
			this.c = c;
			this.dkLen = dkLen;
			this.dk = dk;
		}
	}
}
