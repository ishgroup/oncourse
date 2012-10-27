package ish.oncourse.enrol.checkout;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.ioc.MessageFormatter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static ish.oncourse.enrol.pages.Checkout.DATE_FIELD_FORMAT;
import static org.junit.Assert.*;

public class ConcessionValidatorTest {

	@Test
	public void test() {

		ConcessionValidator concessionValidator = createValidator();

		concessionValidator.setRequest(createValidRequest());

		concessionValidator.validate();

		assertNotNull(concessionValidator.getConcessionType());
		assertNotNull(concessionValidator.getNumber());
		assertNotNull(concessionValidator.getExpiry());
		assertTrue(concessionValidator.getErrors().isEmpty());

		concessionValidator = createValidator();
		concessionValidator.setRequest(createInvalidRequest(null));
		concessionValidator.validate();
		assertNull(concessionValidator.getNumber());
		assertNull(concessionValidator.getExpiry());
		assertFalse(concessionValidator.getErrors().isEmpty());

		concessionValidator = createValidator();
		concessionValidator.setRequest(createInvalidRequest("01/01/2001"));
		concessionValidator.validate();
		assertNull(concessionValidator.getNumber());
		assertNotNull(concessionValidator.getExpiry());
		assertFalse(concessionValidator.getErrors().isEmpty());

		concessionValidator = createValidator();
		concessionValidator.setRequest(createInvalidRequest("Sun Jan 12 00:00:00 FET 2003"));
		concessionValidator.validate();
		assertNull(concessionValidator.getNumber());
		assertNull(concessionValidator.getExpiry());
		assertFalse(concessionValidator.getErrors().isEmpty());
	}

	private ConcessionValidator createValidator() {
		ConcessionValidator concessionValidator = new ConcessionValidator();

		ConcessionType concessionType = new ConcessionType();
		concessionType.setName("concessionType");
		concessionType.setHasExpiryDate(Boolean.TRUE);
		concessionType.setHasConcessionNumber(Boolean.TRUE);
		concessionValidator.setConcessionType(concessionType);
		concessionValidator.setMessages(createMessages());
		concessionValidator.setDateFormat(FormatUtils.getDateFormat(DATE_FIELD_FORMAT, null));
		return concessionValidator;
	}

	private Messages createMessages() {
		return new Messages() {
			@Override
			public boolean contains(String key) {
				return false;
			}

			@Override
			public String get(String key) {
				return key;
			}

			@Override
			public MessageFormatter getFormatter(String key) {
				return null;
			}

			@Override
			public String format(String key, Object... args) {
				return null;
			}
		};
	}

	private Request createInvalidRequest(final String date) {
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
				if (name.equals(ConcessionValidator.FIELD_NAME_CONCESSION_AGREE))
					return null;
				if (name.equals(ConcessionValidator.FIELD_NAME_CONCESSION_EXPIRY))
					return date;
				if (name.equals(ConcessionValidator.FIELD_NAME_CONCESSION_NUMBER))
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
				if (name.equals(ConcessionValidator.FIELD_NAME_CONCESSION_AGREE))
					return "on";
				if (name.equals(ConcessionValidator.FIELD_NAME_CONCESSION_EXPIRY))
					return "01/01/2100";
				if (name.equals(ConcessionValidator.FIELD_NAME_CONCESSION_NUMBER))
					return "NUMBER";
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
