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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


public class AuthenticationUtil {

	private static final int ITERATIONS = 3;
	private static final int MEMORY = 2 ^ 15; // 32Mb RAM
	private static final int THREADS = 8;
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationUtil.class);
	private static final PasswordEncoder encoder = new Argon2PasswordEncoder(16, 32, THREADS, MEMORY, ITERATIONS);

	public static String generatePasswordHash(String password) {
		Instant start = Instant.now();
		String hash = encoder.encode(password);
		Instant end = Instant.now();
		LOGGER.info("Calculated password hash in {} ms", ChronoUnit.MILLIS.between(start, end));
		return hash;
	}

	/**
	 * Computes password hash and checks if it matches stored value.
	 */
	public static boolean checkPassword(String password, String hash) {
		if (hash.startsWith("$")) {
			return encoder.matches(password, hash);
		} else {
			try {
				return PasswordUtil.validateOldPassword(password, hash);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				throw new RuntimeException("Password cannot be verified.", e);
			}
		}
	}

	/**
	 * Return true if the hash should be upgraded to a more secure encoding
	 * @param hash
	 * @return
	 */
	public static boolean upgradeEncoding(String hash) {
		// new password algorithms all generate hashes starting with '$'
		if (!hash.startsWith("$")) {
			LOGGER.warn("Upgrading old pasword hash.");
			return true;
		}
		boolean upgrade = encoder.upgradeEncoding(hash);
		if (upgrade) {
			LOGGER.warn("Upgrading pasword hash with lower iterations or memory.");
		}
		return upgrade;
	}
}
