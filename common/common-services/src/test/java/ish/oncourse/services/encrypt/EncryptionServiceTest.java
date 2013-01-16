package ish.oncourse.services.encrypt;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import ish.oncourse.services.ServiceModule;
import ish.oncourse.test.ServiceTest;

public class EncryptionServiceTest extends ServiceTest {
	
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
			assertFalse("Encryption lead to exception", true);
		}
		String result = null;
		try {
			result = service.decrypt(encryptedData);
		} catch (Exception e) {
			assertFalse("Decryption lead to exception", true);
		}
		assertEquals("Original data should be the same as after encrypt and decrypt", data, result);
	}
	
}
