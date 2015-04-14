package ish.oncourse.services.encrypt;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class EncryptionService {
	private final static Logger logger = LogManager.getLogger();
	private Key key;
	private Cipher cipher;

	public EncryptionService() throws Exception {
		cipher = Cipher.getInstance("AES");
		key = new SecretKeySpec("E072EDF9534053A0".getBytes("UTF-8"), "AES");
	}

	public byte[] encrypt(String input) throws Exception {
		if (key == null || cipher == null) {
			logger.error("Some problem with encryption init");
			return null;
		}
		byte[] inputBytes = input.getBytes("UTF-8");

		cipher.init(Cipher.ENCRYPT_MODE, key);

		byte[] encrypted = cipher.doFinal(inputBytes);

		return Base64.encodeBase64(encrypted);
	}

	public String decrypt(byte[] encryptionBytes) throws Exception {
		if (key == null || cipher == null) {
			logger.error("Some problem with encryption init");
			return null;
		}

		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] recoveredBytes = cipher.doFinal(Base64.decodeBase64(encryptionBytes));
		String recovered = new String(recoveredBytes);
		return recovered;
	}

}
