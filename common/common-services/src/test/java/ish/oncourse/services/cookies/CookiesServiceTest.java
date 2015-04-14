package ish.oncourse.services.cookies;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CookiesServiceTest {

	private static final String NUM_DEC_VALUE = "25.5";
	private static final String NUM_VALUE = "25";
	private static final String STRING_VALUE = "String val";

	@Test
	public void convertToInstanceTest() {
		CookiesService cookiesService = new CookiesService();
		assertNotNull(cookiesService.convertToInstance(STRING_VALUE, String.class));
		assertNull(cookiesService.convertToInstance(STRING_VALUE, Long.class));
		assertNotNull(cookiesService.convertToInstance(NUM_VALUE, Long.class));
		assertNotNull(cookiesService.convertToInstance(NUM_VALUE, Byte.class));
		assertNull(cookiesService.convertToInstance(NUM_DEC_VALUE, Long.class));
		assertNotNull(cookiesService.convertToInstance(NUM_DEC_VALUE, Double.class));
		assertNotNull(cookiesService.convertToInstance(NUM_DEC_VALUE, Float.class));
		assertNotNull(cookiesService.convertToInstance(NUM_VALUE, Integer.class));
		assertNotNull(cookiesService.convertToInstance(NUM_VALUE, Short.class));
		assertNull(cookiesService.convertToInstance("25.5.5", Long.class));
	}
}
