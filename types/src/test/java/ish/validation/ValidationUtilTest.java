package ish.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;




public class ValidationUtilTest {
	
	private static final String[] validEmails = { 
			"testEmail@domain.org", 
			"test+Email@domain.org", 
			"test.Email@domain.org",
			"test/Email@domain.org", 
			"test|Email@domain.org", 
			"test_Email@domain.org", 
			"jason.riley@cce.sydney" ,
			"testEmail@domain",
			"testEmail@111.111.111.111", 
			"test\\Email@domain.org"};
	
	private static final String[] invalidEmails = {
			null,
			"",
			"testEmail-domain.org",
			"@domain.org",
			"testEmail@",
			"test_Email",
			"testEmail@domain./org",
			"testEmail@domain.|org",
			"testEmail@domain.org.",
			"testEmail@domain.org?",
			"testEmail@domain.org,com",
			"testEmail@domain.\\org",
			"testEmail@domain.org,,,",
			"testEmail@domain.org,;"
	};

	@Test
	public void testEmail() {
		for (String validEmail : validEmails) {
			Assertions.assertTrue(ValidationUtil.isValidEmailAddress(validEmail), String.format("This email should be valid %s", validEmail));
		}

		for (String invalidEmail : invalidEmails) {
			Assertions.assertFalse(ValidationUtil.isValidEmailAddress(invalidEmail), String.format("This email should be invalid %s", invalidEmail));
		}
	}
}
