package ish.oncourse.webservices.soap.interceptors;

import ish.oncourse.model.College;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.exception.AuthSoapFault;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.tapestry5.services.Request;
import org.junit.Test;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SecurityCodeInterceptorTest {

	@Test
	public void testSuccessResult() throws NoSuchFieldException, IllegalAccessException {
		Element element = mock(Element.class);
		when(element.getTextContent()).thenReturn("SecurityCode");

		Header header = mock(Header.class);
		when(header.getObject()).thenReturn(element);
		QName qName = new QName("SecurityCode");

		SoapMessage soapMessage = mock(SoapMessage.class);
		when(soapMessage.getHeader(qName)).thenReturn(header);

		College college = mock(College.class);
		when(college.getId()).thenReturn(1000l);
		Request request = mock(Request.class);
		ICollegeService collegeService = mock(ICollegeService.class);
		when(collegeService.findBySecurityCode("SecurityCode")).thenReturn(college);

		SecurityCodeInterceptor interceptor = new SecurityCodeInterceptor();
		forceSet(interceptor, "request", request);
		forceSet(interceptor, "collegeService", collegeService);

		try
		{
			interceptor.handleMessage(soapMessage);
			assertTrue(true);
		}
		catch (AuthSoapFault e)
		{
			assertFalse(true);
		}

	}

	@Test
	public void testInvalidSecurityCode() throws NoSuchFieldException, IllegalAccessException {
		Element element = mock(Element.class);
		when(element.getTextContent()).thenReturn("SecurityCode");

		Header header = mock(Header.class);
		when(header.getObject()).thenReturn(element);
		QName qName = new QName("SecurityCode");

		SoapMessage soapMessage = mock(SoapMessage.class);
		when(soapMessage.getHeader(qName)).thenReturn(header);

		Request request = mock(Request.class);
		ICollegeService collegeService = mock(ICollegeService.class);
		when(collegeService.findBySecurityCode("SecurityCode")).thenReturn(null);

		SecurityCodeInterceptor interceptor = new SecurityCodeInterceptor();
		forceSet(interceptor, "request", request);
		forceSet(interceptor, "collegeService", collegeService);

		try
		{
			interceptor.handleMessage(soapMessage);
			assertFalse(true);
		}
		catch (AuthSoapFault e)
		{
			assertNotNull(e);
			assertTrue(e.getMessage().contains(String.format(SecurityCodeInterceptor.ERROR_TEMPLATE_invalidSecurityCode, "SecurityCode", "unknown", null)));
		}

	}

	@Test
	public void testEmptySecurityCode() throws NoSuchFieldException, IllegalAccessException {


		SoapMessage soapMessage = mock(SoapMessage.class);

		Request request = mock(Request.class);
		ICollegeService collegeService = mock(ICollegeService.class);

		SecurityCodeInterceptor interceptor = new SecurityCodeInterceptor();
		forceSet(interceptor, "request", request);
		forceSet(interceptor, "collegeService", collegeService);

		try
		{
			interceptor.handleMessage(soapMessage);
			assertFalse(true);
		}
		catch (AuthSoapFault e)
		{
			assertNotNull(e);
			assertTrue(e.getMessage().contains(String.format(SecurityCodeInterceptor.ERROR_TEMPLATE_emptySecurityCode, "unknown", null)));
		}

	}

	void forceSet(SecurityCodeInterceptor interceptor, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
		Class<?> clazz = interceptor.getClass();
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(interceptor, value);
	}
}
