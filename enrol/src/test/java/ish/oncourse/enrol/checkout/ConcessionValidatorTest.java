package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.services.Constants;
import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.MessageFormatter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConcessionValidatorTest {

	@Test
	public void test() {


		ConcessionParser parser = createParser();

		parser.setRequest(createRequest("NUMBER", "12/10/2073", "on"));

		parser.parse();

		assertNotNull(parser.getStudentConcession().getConcessionType());
		assertNotNull(parser.getStudentConcession().getConcessionNumber());
		assertNotNull(parser.getStudentConcession().getExpiresOn());
		assertTrue(parser.getErrors().isEmpty());

		parser = createParser();
		parser.setRequest(createRequest(null, null, null));
		parser.parse();
		assertNull(parser.getStudentConcession().getConcessionNumber());
		assertNull(parser.getStudentConcession().getExpiresOn());
		assertFalse(parser.getErrors().isEmpty());

		parser = createParser();
		parser.setRequest(createRequest(null, "01/01/2001", null));
		parser.parse();
		assertNull(parser.getStudentConcession().getConcessionNumber());
		assertNotNull(parser.getStudentConcession().getExpiresOn());
		assertFalse(parser.getErrors().isEmpty());

		parser = createParser();
		parser.setRequest(createRequest(null, "Sun Jan 12 00:00:00 FET 2003", null));
		parser.parse();
		assertNull(parser.getStudentConcession().getConcessionNumber());
		assertNull(parser.getStudentConcession().getExpiresOn());
		assertFalse(parser.getErrors().isEmpty());
	}

	private ConcessionParser createParser() {
		ConcessionParser parser = new ConcessionParser();

		ConcessionType concessionType = new ConcessionType();
		concessionType.setName("concessionType");
		concessionType.setHasExpiryDate(Boolean.TRUE);
		concessionType.setHasConcessionNumber(Boolean.TRUE);
		StudentConcession studentConcession = spy(new StudentConcession());
		when(studentConcession.getConcessionType()).thenReturn(concessionType);
		parser.setStudentConcession(studentConcession);
		parser.setMessages(createMessages());
		parser.setDateFormat(FormatUtils.getDateFormat(Constants.DATE_FIELD_PARSE_FORMAT, StringUtils.EMPTY));
		return parser;
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

	private Request createRequest(final String concessionNumber, final String expiresOn, final String concessionAgree) {
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

				ConcessionParser.Field field = ConcessionParser.Field.valueOf(name);
				switch (field) {
					case concessionNumber:
						return concessionNumber;
					case expiresOn:
						return expiresOn;
					case concessionAgree:
						return concessionAgree;
					default:
						throw new IllegalArgumentException();
				}
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
