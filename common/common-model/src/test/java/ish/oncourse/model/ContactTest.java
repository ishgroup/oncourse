package ish.oncourse.model;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for {@link Contact} entity.
 * 
 * @author ksenia
 * 
 */
public class ContactTest {

	/**
	 * Entity to test.
	 */
	private static Contact contact;

	/**
	 * Array of valid emails. {@see #validateEmailSuccessTest()}.
	 */
	private String[] validEmails = { "testEmail@domain.org", "test+Email@domain.org", "test.Email@domain.org",
			"test/Email@domain.org", "test_Email@domain.org" };

	/**
	 * Array of invalid emails. {@see #validateEmailFailedTest()}.
	 */
	private String[] invalidEmails = { "testEmail-domain.org", "@domain.org", "testEmail@domain", "test_Email",
			"testEmail@111.111.111.111" };

	@BeforeClass
	public static void init() {
		contact = new Contact();
	}

	/**
	 * Emulates the situations when email is null or empty.
	 */
	@Test
	public void validateEmailEmptyTest() {
		String result = contact.validateEmail();
		assertEquals(contact.getEmptyEmailMessage(), result);
		contact.setEmailAddress("");
		assertEquals(contact.getEmptyEmailMessage(), result);
	}

	/**
	 * Emulates the situations when email is valid string. In these cases
	 * testing method should return null.
	 */
	@Test
	public void validateEmailSuccessTest() {
		for (String email : validEmails) {
			contact.setEmailAddress(email);
			String result = contact.validateEmail();
			assertNull("Email address \"" + email + "\" failed with message:" + result, result);
		}
	}

	/**
	 * Emulates the situations when email is invalid string. In these cases
	 * testing method should return error message.
	 */
	@Test
	public void validateEmailFailedTest() {
		for (String email : invalidEmails) {
			contact.setEmailAddress(email);
			String result = contact.validateEmail();
			assertEquals("Error message for email \"" + email + "\" is incorrect:" + result,
					Contact.INVALID_EMAIL_MESSAGE, result);
		}
	}
}
