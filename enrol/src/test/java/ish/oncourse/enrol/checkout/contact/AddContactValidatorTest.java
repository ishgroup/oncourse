package ish.oncourse.enrol.checkout.contact;

import org.apache.tapestry5.services.Request;
import org.junit.Test;

import static ish.oncourse.enrol.checkout.contact.AddContactValidator.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class AddContactValidatorTest {

	@Test
	public void testValidRequest()
	{

		Request request = mock(Request.class);
		when(request.getParameter(FIELD_NAME_firstName)).thenReturn(FIELD_NAME_firstName);
		when(request.getParameter(FIELD_NAME_lastName)).thenReturn(FIELD_NAME_lastName);
		when(request.getParameter(FIELD_NAME_email)).thenReturn("test@ish.com.au");

		AddContactValidator validator = createParser(request);
		validator.validate();

		assertNotNull(validator.getContactCredentials().getFirstName());
		assertNotNull(validator.getContactCredentials().getLastName());
		assertNotNull(validator.getContactCredentials().getEmail());
		assertTrue(validator.getErrors().isEmpty());
	}

	@Test
	public void testNullParametersRequest()
	{
		Request request = mock(Request.class);
		when(request.getParameter(FIELD_NAME_firstName)).thenReturn(null);
		when(request.getParameter(FIELD_NAME_lastName)).thenReturn(null);
		when(request.getParameter(FIELD_NAME_email)).thenReturn(null);

		AddContactValidator validator = createParser(request);
		validator.validate();

		assertNull(validator.getContactCredentials().getFirstName());
		assertNull(validator.getContactCredentials().getLastName());
		assertNull(validator.getContactCredentials().getEmail());
		assertFalse(validator.getErrors().isEmpty());

	}

	@Test
	public void testEmptyParametersRequest()
	{
		Request request = mock(Request.class);
		when(request.getParameter(FIELD_NAME_firstName)).thenReturn("");
		when(request.getParameter(FIELD_NAME_lastName)).thenReturn(" ");
		when(request.getParameter(FIELD_NAME_email)).thenReturn("  ");

		AddContactValidator validator = createParser(request);
		validator.validate();

		assertNull(validator.getContactCredentials().getFirstName());
		assertNull(validator.getContactCredentials().getLastName());
		assertNull(validator.getContactCredentials().getEmail());
		assertFalse(validator.getErrors().isEmpty());

	}

	private AddContactValidator createParser(Request request) {
		AddContactValidator validator = AddContactValidator.valueOf(new ContactCredentials(), request, false);
		assertNotNull(validator.getContactCredentials());
		assertNotNull(validator.getRequest());
		assertNotNull(validator.getErrors());
		return validator;
	}
}
