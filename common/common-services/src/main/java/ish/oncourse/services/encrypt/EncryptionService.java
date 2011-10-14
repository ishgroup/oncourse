package ish.oncourse.services.encrypt;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class EncryptionService {
	private final static Logger LOGGER = Logger.getLogger(EncryptionService.class);
	private Key key;
	private Cipher cipher;

	public EncryptionService() throws Exception {
		cipher = Cipher.getInstance("AES");
		key = new SecretKeySpec("E072EDF9534053A0B6C581C58FBF25CC".getBytes("UTF-8"), "AES");
	}

	public byte[] encrypt(String input) throws Exception {
		if (key == null || cipher == null) {
			LOGGER.error("Some problem with encryption init");
			return null;
		}
		byte[] inputBytes = input.getBytes("UTF-8");

		cipher.init(Cipher.ENCRYPT_MODE, key);

		byte[] encrypted = cipher.doFinal(inputBytes);

		return Base64.encodeBase64(encrypted);
	}

	public String decrypt(byte[] encryptionBytes) throws Exception {
		if (key == null || cipher == null) {
			LOGGER.error("Some problem with encryption init");
			return null;
		}

		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] recoveredBytes = cipher.doFinal(Base64.decodeBase64(encryptionBytes));
		String recovered = new String(recoveredBytes);
		return recovered;
	}

}
