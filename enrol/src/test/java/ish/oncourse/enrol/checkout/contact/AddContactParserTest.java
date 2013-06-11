package ish.oncourse.enrol.checkout.contact;

import org.apache.tapestry5.services.Request;
import org.junit.Test;

import static ish.oncourse.enrol.checkout.contact.AddContactParser.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class AddContactParserTest {

	@Test
	public void testValidRequest()
	{

		Request request = mock(Request.class);
		when(request.getParameter(FIELD_NAME_firstName)).thenReturn(FIELD_NAME_firstName);
		when(request.getParameter(FIELD_NAME_lastName)).thenReturn(FIELD_NAME_lastName);
		when(request.getParameter(FIELD_NAME_email)).thenReturn("test@ish.com.au");

		AddContactParser parser = createParser(request);
		parser.parse();

		assertNotNull(parser.getContactCredentials().getFirstName());
		assertNotNull(parser.getContactCredentials().getLastName());
		assertNotNull(parser.getContactCredentials().getEmail());
		assertTrue(parser.getErrors().isEmpty());
	}

	@Test
	public void testNullParametersRequest()
	{
		Request request = mock(Request.class);
		when(request.getParameter(FIELD_NAME_firstName)).thenReturn(null);
		when(request.getParameter(FIELD_NAME_lastName)).thenReturn(null);
		when(request.getParameter(FIELD_NAME_email)).thenReturn(null);

		AddContactParser parser = createParser(request);
		parser.parse();

		assertNull(parser.getContactCredentials().getFirstName());
		assertNull(parser.getContactCredentials().getLastName());
		assertNull(parser.getContactCredentials().getEmail());
		assertFalse(parser.getErrors().isEmpty());

	}

	@Test
	public void testEmptyParametersRequest()
	{
		Request request = mock(Request.class);
		when(request.getParameter(FIELD_NAME_firstName)).thenReturn("");
		when(request.getParameter(FIELD_NAME_lastName)).thenReturn(" ");
		when(request.getParameter(FIELD_NAME_email)).thenReturn("  ");

		AddContactParser parser = createParser(request);
		parser.parse();

		assertNull(parser.getContactCredentials().getFirstName());
		assertNull(parser.getContactCredentials().getLastName());
		assertNull(parser.getContactCredentials().getEmail());
		assertFalse(parser.getErrors().isEmpty());

	}

	private AddContactParser createParser(Request request) {
		AddContactParser parser = new AddContactParser();
		parser.setContactCredentials(new ContactCredentials());
		parser.setRequest(request);
		assertNotNull(parser.getContactCredentials());
		assertNotNull(parser.getRequest());
		assertNotNull(parser.getErrors());
		return parser;
	}
}
