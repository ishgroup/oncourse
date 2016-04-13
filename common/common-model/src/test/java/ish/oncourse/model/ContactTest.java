package ish.oncourse.model;

import ish.oncourse.utils.ContactDelegator;
import ish.validation.ContactValidator;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
	private Contact contact;

	/**
	 * Array of valid emails. {@see #validateEmailSuccessTest()}.
	 */
	private String[] validEmails = { "testEmail@domain.org", "test+Email@domain.org", "test.Email@domain.org", "testEmail@domain",
			"test/Email@domain.org", "test|Email@domain.org", "test_Email@domain.org", "jason.riley@cce.sydney" ,
			"testEmail@111.111.111.111" , "test\\Email@domain.org"};

	/**
	 * Array of invalid emails. {@see #validateEmailFailedTest()}.
	 */
	private String[] invalidEmails = { "testEmail-domain.org", "@domain.org", "testEmail@", "test_Email", "testEmail@domain./org", "testEmail@domain.|org", "testEmail@domain.\\org"};

	@Before
	public void init() {
		contact = new Contact();
	}

	/**
	 * Emulates the situations when email is null or empty.
	 */
	@Test
	public void validateEmailEmptyTest() {
		String result = contact.validateEmail();
		assertEquals(contact.getEmptyEmailMessage(), result);
		contact.setEmailAddress(StringUtils.EMPTY);
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

	@Test
	public void testValidateForSave() {
		EntityResolver entityResolver = mock(EntityResolver.class);
		ObjectContext objectContext = mock(ObjectContext.class);
		when(objectContext.getEntityResolver()).thenReturn(entityResolver);

		Contact contact = mock(Contact.class);
		when(contact.getObjectContext()).thenReturn(objectContext);

		when(contact.getGivenName()).thenReturn(StringUtils.repeat("a", 500));

		ValidationResult validationResult = new ValidationResult();
		ContactValidator contactValidator = ContactValidator.valueOf(ContactDelegator.valueOf(contact), validationResult);
		contactValidator.validate();
		assertTrue(validationResult.hasFailures());
	}

	/**
	 * test incorrect property length
	 */
	@Test
	public void testIncorrectPropertyLengthValidation() {
		EntityResolver entityResolver = mock(EntityResolver.class);
		ObjectContext objectContext = mock(ObjectContext.class);
		when(objectContext.getEntityResolver()).thenReturn(entityResolver);

		Contact contact = mock(Contact.class);
		when(contact.getObjectContext()).thenReturn(objectContext);

		when(contact.getGivenName()).thenReturn(StringUtils.repeat("a", 129));
		when(contact.getFamilyName()).thenReturn( StringUtils.repeat("a", 129));
		when(contact.getPostcode()).thenReturn(StringUtils.repeat("a", 21));
		when(contact.getState()).thenReturn(StringUtils.repeat("a", 21));
		when(contact.getMobilePhoneNumber()).thenReturn(StringUtils.repeat("a", 21));
		when(contact.getHomePhoneNumber()).thenReturn( StringUtils.repeat("a", 21));
		when(contact.getFaxNumber()).thenReturn(StringUtils.repeat("a", 21));
		when(contact.getEmailAddress()).thenReturn(StringUtils.repeat("email", 30).concat("@com.au"));

		ValidationResult validationResult = new ValidationResult();
		ContactValidator contactValidator = ContactValidator.valueOf(ContactDelegator.valueOf(contact), validationResult);
		contactValidator.validate();
		assertTrue(validationResult.hasFailures());
		assertEquals(8, validationResult.getFailures().size());
	}

	@Test
	public void testValidateForSaveBirthDate1() {
		EntityResolver entityResolver = mock(EntityResolver.class);
		ObjectContext objectContext = mock(ObjectContext.class);
		when(objectContext.getEntityResolver()).thenReturn(entityResolver);

		Contact contact = mock(Contact.class);
		when(contact.getObjectContext()).thenReturn(objectContext);

		when(contact.getGivenName()).thenReturn("first");
		when(contact.getFamilyName()).thenReturn("last");
		when(contact.getDateOfBirth()).thenReturn(new Date());

		ValidationResult validationResult = new ValidationResult();
		ContactValidator contactValidator = ContactValidator.valueOf(ContactDelegator.valueOf(contact), validationResult);
		contactValidator.validate();
		assertTrue(validationResult.hasFailures());
		assertEquals(1, validationResult.getFailures().size());
	}

	@Test
	public void testValidateForSaveBirthDate2() {
		EntityResolver entityResolver = mock(EntityResolver.class);
		ObjectContext objectContext = mock(ObjectContext.class);
		when(objectContext.getEntityResolver()).thenReturn(entityResolver);

		Contact contact = mock(Contact.class);
		when(contact.getObjectContext()).thenReturn(objectContext);

		when(contact.getGivenName()).thenReturn("first");
		when(contact.getFamilyName()).thenReturn("last");
		when(contact.getDateOfBirth()).thenReturn(DateUtils.addDays(new Date(), 1));

		ValidationResult validationResult = new ValidationResult();
		ContactValidator contactValidator = ContactValidator.valueOf(ContactDelegator.valueOf(contact), validationResult);
		contactValidator.validate();
		assertTrue(validationResult.hasFailures());
		assertEquals(1, validationResult.getFailures().size());
	}

	@Test
	public void testValidateForSaveBirthDate3() {
		EntityResolver entityResolver = mock(EntityResolver.class);
		ObjectContext objectContext = mock(ObjectContext.class);
		when(objectContext.getEntityResolver()).thenReturn(entityResolver);

		Contact contact = mock(Contact.class);
		when(contact.getObjectContext()).thenReturn(objectContext);

		when(contact.getGivenName()).thenReturn("first");
		when(contact.getFamilyName()).thenReturn("last");
		when(contact.getDateOfBirth()).thenReturn(DateUtils.addDays(new Date(), -1));

		ValidationResult validationResult = new ValidationResult();
		ContactValidator contactValidator = ContactValidator.valueOf(ContactDelegator.valueOf(contact), validationResult);
		contactValidator.validate();
		assertEquals(0, validationResult.getFailures().size());
	}

	@Test
	public void testValidateContactWithoutNameValidation() {
		EntityResolver entityResolver = mock(EntityResolver.class);
		ObjectContext objectContext = mock(ObjectContext.class);
		when(objectContext.getEntityResolver()).thenReturn(entityResolver);

		Contact contact = mock(Contact.class);
		when(contact.getObjectContext()).thenReturn(objectContext);

		when(contact.getGivenName()).thenReturn(null);
		when(contact.getFamilyName()).thenReturn(null);

		ValidationResult validationResult = new ValidationResult();
		ContactValidator contactValidator = ContactValidator.valueOf(ContactDelegator.valueOf(contact), validationResult);
		contactValidator.validate();
		assertEquals(2, validationResult.getFailures().size());
	}

	@Test
	public void testValidateCompanyValidation() {
		EntityResolver entityResolver = mock(EntityResolver.class);
		ObjectContext objectContext = mock(ObjectContext.class);
		when(objectContext.getEntityResolver()).thenReturn(entityResolver);

		Contact contact = mock(Contact.class);
		when(contact.getObjectContext()).thenReturn(objectContext);

		when(contact.getFamilyName()).thenReturn("CompanyName");
		when(contact.getIsCompany()).thenReturn(true);

		ValidationResult validationResult = new ValidationResult();
		ContactValidator contactValidator = ContactValidator.valueOf(ContactDelegator.valueOf(contact), validationResult);
		contactValidator.validate();
		assertEquals(0, validationResult.getFailures().size());
	}

	@Test
	public void testIncorrectCompanyNameValidation() throws Exception {
		EntityResolver entityResolver = mock(EntityResolver.class);
		ObjectContext objectContext = mock(ObjectContext.class);
		when(objectContext.getEntityResolver()).thenReturn(entityResolver);

		Contact contact = mock(Contact.class);
		when(contact.getObjectContext()).thenReturn(objectContext);

		when(contact.getFamilyName()).thenReturn("");
		when(contact.getIsCompany()).thenReturn(true);

		ValidationResult validationResult = new ValidationResult();
		ContactValidator contactValidator = ContactValidator.valueOf(ContactDelegator.valueOf(contact), validationResult);
		contactValidator.validate();
		assertEquals(1, validationResult.getFailures().size());
	}

	@Test
	public void testIncorrectEmailValidation() throws Exception {
		EntityResolver entityResolver = mock(EntityResolver.class);
		ObjectContext objectContext = mock(ObjectContext.class);
		when(objectContext.getEntityResolver()).thenReturn(entityResolver);

		Contact contact = mock(Contact.class);
		when(contact.getObjectContext()).thenReturn(objectContext);

		when(contact.getFamilyName()).thenReturn("familyName");
		when(contact.getGivenName()).thenReturn("givenName");
		when(contact.getEmailAddress()).thenReturn("test@com.au@au");

		ValidationResult validationResult = new ValidationResult();
		ContactValidator contactValidator = ContactValidator.valueOf(ContactDelegator.valueOf(contact), validationResult);
		contactValidator.validate();
		assertEquals(1, validationResult.getFailures().size());
	}

	@Test
	public void testCorrectEmailValidation() throws Exception {
		EntityResolver entityResolver = mock(EntityResolver.class);
		ObjectContext objectContext = mock(ObjectContext.class);
		when(objectContext.getEntityResolver()).thenReturn(entityResolver);

		Contact contact = mock(Contact.class);
		when(contact.getObjectContext()).thenReturn(objectContext);

		when(contact.getFamilyName()).thenReturn("familyName");
		when(contact.getGivenName()).thenReturn("givenName");
		when(contact.getEmailAddress()).thenReturn("test@com.au");

		ValidationResult validationResult = new ValidationResult();
		ContactValidator contactValidator = ContactValidator.valueOf(ContactDelegator.valueOf(contact), validationResult);
		contactValidator.validate();
		assertEquals(0, validationResult.getFailures().size());
	}

	@Test
	public void testContactWithNullFieldsValidation() throws Exception {
		EntityResolver entityResolver = mock(EntityResolver.class);
		ObjectContext objectContext = mock(ObjectContext.class);
		when(objectContext.getEntityResolver()).thenReturn(entityResolver);

		Contact contact = mock(Contact.class);
		when(contact.getObjectContext()).thenReturn(objectContext);

		when(contact.getFamilyName()).thenReturn(null);
		when(contact.getGivenName()).thenReturn(null);
		when(contact.getPostcode()).thenReturn(null);
		when(contact.getState()).thenReturn(null);
		when(contact.getMobilePhoneNumber()).thenReturn(null);
		when(contact.getHomePhoneNumber()).thenReturn(null);
		when(contact.getFaxNumber()).thenReturn(null);
		when(contact.getEmailAddress()).thenReturn(null);

		ValidationResult validationResult = new ValidationResult();
		ContactValidator contactValidator = ContactValidator.valueOf(ContactDelegator.valueOf(contact), validationResult);
		contactValidator.validate();
		assertEquals(2, validationResult.getFailures().size());
	}
}
