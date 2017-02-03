package ish.oncourse.model;

import ish.oncourse.utils.ContactDelegator;
import ish.validation.ContactErrorCode;
import ish.validation.ContactValidator;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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

		ContactValidator contactValidator = ContactValidator.valueOf(ContactDelegator.valueOf(contact));
		Map<String, ContactErrorCode> validate = contactValidator.validate();
		assertEquals(8, validate.size());
	}
}
