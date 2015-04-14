package ish.oncourse.webservices.soap.v4.interceptors;

import ish.oncourse.webservices.exception.AuthSoapFault;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.binding.soap.Soap12;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.common.util.SOAPConstants;
import org.apache.cxf.message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.*;

public class InterceptorErrorHandleTest {

	private static final String TEST_MESSAGE_0 = "MESSAGE";

	private static final String TEST_MESSAGE_1 = "(SoapAction: SOAPAction; basePath: org.apache.cxf.message.Message.BASE_PATH; requestUrl: org.apache.cxf.request.url)";
	private static final String TEST_MESSAGE_2 = "java.lang.IllegalArgumentException";

	private static final Logger logger = LogManager.getLogger();

	private InterceptorErrorHandle createHandle() {
		SoapMessage message = new SoapMessage(Soap12.getInstance());
		message.put(SOAPConstants.SOAP_ACTION, SOAPConstants.SOAP_ACTION);
		message.put(Message.BASE_PATH, Message.BASE_PATH);
		message.put(Message.REQUEST_URL, Message.REQUEST_URL);
		return new InterceptorErrorHandle(message, logger);
	}

	@Test
	public void testHandle_throwable_message() {
		InterceptorErrorHandle interceptorErrorHandle = createHandle();
		AuthSoapFault authSoapFault = interceptorErrorHandle.handle(new IllegalArgumentException(), TEST_MESSAGE_0);
		assertErrorMessage(authSoapFault, TEST_MESSAGE_0, TEST_MESSAGE_1, TEST_MESSAGE_2);
	}

	private void assertErrorMessage(AuthSoapFault authSoapFault, String... expectedMessage) {
		assertNotNull(authSoapFault);
		String message = authSoapFault.getMessage();
		String[] messages = StringUtils.split(message, '\n');

		assertTrue(messages.length >= expectedMessage.length);

		for (int i = 0; i < expectedMessage.length; i++) {
			String em = expectedMessage[i];
			String m = messages[i];
			assertEquals(String.format("assert string %d", i), em, m);
		}
	}


	@Test
	public void testHandle_throwable() {
		InterceptorErrorHandle interceptorErrorHandle = createHandle();
		AuthSoapFault authSoapFault = interceptorErrorHandle.handle(new IllegalArgumentException());
		assertErrorMessage(authSoapFault, InterceptorErrorHandle.DEFAULT_MESSAGE, TEST_MESSAGE_1, TEST_MESSAGE_2);
	}


	@Test
	public void testHandle_message() {
		InterceptorErrorHandle interceptorErrorHandle = createHandle();
		AuthSoapFault authSoapFault = interceptorErrorHandle.handle(TEST_MESSAGE_0);
		assertErrorMessage(authSoapFault, TEST_MESSAGE_0, TEST_MESSAGE_1);
	}

}
