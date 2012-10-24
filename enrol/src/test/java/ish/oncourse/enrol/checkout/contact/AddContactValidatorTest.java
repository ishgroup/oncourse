package ish.oncourse.enrol.checkout.contact;

import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class AddContactValidatorTest {

	@Test
	public void test()
	{
		AddContactValidator validator = new AddContactValidator();
		validator.setContactCredentials(new ContactCredentials());
		validator.setRequest(createValidRequest());

		assertNotNull(validator.getContactCredentials());
		assertNotNull(validator.getRequest());
		assertNotNull(validator.getErrors());
		validator.validate();

		assertNotNull(validator.getContactCredentials().getFirstName());
		assertNotNull(validator.getContactCredentials().getLastName());
		assertNotNull(validator.getContactCredentials().getEmail());
		assertTrue(validator.getErrors().isEmpty());

		validator = new AddContactValidator();
		validator.setContactCredentials(new ContactCredentials());
		validator.setRequest(createInvalidRequest());
		assertNotNull(validator.getContactCredentials());
		assertNotNull(validator.getRequest());
		assertNotNull(validator.getErrors());
		validator.validate();
		assertNull(validator.getContactCredentials().getFirstName());
		assertNull(validator.getContactCredentials().getLastName());
		assertNull(validator.getContactCredentials().getEmail());
		assertFalse(validator.getErrors().isEmpty());


	}


	private Request createInvalidRequest() {
		return new Request() {
			@Override
			public Session getSession(boolean create) {
				return null;
			}

			@Override
			public String getContextPath() {
				return null;
			}

			@Override
			public List<String> getParameterNames() {
				return null;
			}

			@Override
			public String getParameter(String name) {
				if (name.equals(AddContactValidator.FIELD_NAME_firstName))
					return null;
				if (name.equals(AddContactValidator.FIELD_NAME_lastName))
					return null;
				if (name.equals(AddContactValidator.FIELD_NAME_email))
					return null;
				throw new IllegalArgumentException();
			}

			@Override
			public String[] getParameters(String name) {
				return new String[0];
			}

			@Override
			public String getPath() {
				return null;
			}

			@Override
			public Locale getLocale() {
				return null;
			}

			@Override
			public List<String> getHeaderNames() {
				return null;
			}

			@Override
			public long getDateHeader(String name) {
				return 0;
			}

			@Override
			public String getHeader(String name) {
				return null;
			}

			@Override
			public boolean isXHR() {
				return false;
			}

			@Override
			public boolean isSecure() {
				return false;
			}

			@Override
			public String getServerName() {
				return null;
			}

			@Override
			public boolean isRequestedSessionIdValid() {
				return false;
			}

			@Override
			public Object getAttribute(String name) {
				return null;
			}

			@Override
			public void setAttribute(String name, Object value) {
			}

			@Override
			public String getMethod() {
				return null;
			}

			@Override
			public int getLocalPort() {
				return 0;
			}

			@Override
			public int getServerPort() {
				return 0;
			}
		};
	}

	private Request createValidRequest() {
		return new Request() {
			@Override
			public Session getSession(boolean create) {
				return null;
			}

			@Override
			public String getContextPath() {
				return null;
			}

			@Override
			public List<String> getParameterNames() {
				return null;
			}

			@Override
			public String getParameter(String name) {
				if (name.equals(AddContactValidator.FIELD_NAME_firstName))
					return AddContactValidator.FIELD_NAME_firstName;
				if (name.equals(AddContactValidator.FIELD_NAME_lastName))
					return AddContactValidator.FIELD_NAME_lastName;
				if (name.equals(AddContactValidator.FIELD_NAME_email))
					return "test@ish.com.au";
				throw new IllegalArgumentException();
			}

			@Override
			public String[] getParameters(String name) {
				return new String[0];
			}

			@Override
			public String getPath() {
				return null;
			}

			@Override
			public Locale getLocale() {
				return null;
			}

			@Override
			public List<String> getHeaderNames() {
				return null;
			}

			@Override
			public long getDateHeader(String name) {
				return 0;
			}

			@Override
			public String getHeader(String name) {
				return null;
			}

			@Override
			public boolean isXHR() {
				return false;
			}

			@Override
			public boolean isSecure() {
				return false;
			}

			@Override
			public String getServerName() {
				return null;
			}

			@Override
			public boolean isRequestedSessionIdValid() {
				return false;
			}

			@Override
			public Object getAttribute(String name) {
				return null;
			}

			@Override
			public void setAttribute(String name, Object value) {
			}

			@Override
			public String getMethod() {
				return null;
			}

			@Override
			public int getLocalPort() {
				return 0;
			}

			@Override
			public int getServerPort() {
				return 0;
			}
		};
	}
}
