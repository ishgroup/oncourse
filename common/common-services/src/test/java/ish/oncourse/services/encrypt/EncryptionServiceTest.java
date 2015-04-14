package ish.oncourse.services.encrypt;

import ish.oncourse.services.ServiceModule;
import ish.oncourse.test.ServiceTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EncryptionServiceTest extends ServiceTest {
	private final static Logger logger = LogManager.getLogger();
	private EncryptionService service;
	
	@Before
    public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceModule.class);
		service = new EncryptionService();
	}
	
	@Test
    public void testEncrypt() {
		assertNotNull("Encryptions service should be inited", service);
		String data = "some@test$data";
		byte[] encryptedData = null;
		try {
			encryptedData = service.encrypt(data);
		} catch (Exception e) {
			logger.error("Encryption service thow an exception", e);
			assertFalse("Encryption lead to exception" + e.getMessage() , true);
		}
		String result = null;
		try {
			result = service.decrypt(encryptedData);
		} catch (Exception e) {
			logger.error("Encryption service thow an exception", e);
			assertFalse("Decryption lead to exception" + e.getMessage(), true);
		}
		assertEquals("Original data should be the same as after encrypt and decrypt", data, result);
	}
	
}
