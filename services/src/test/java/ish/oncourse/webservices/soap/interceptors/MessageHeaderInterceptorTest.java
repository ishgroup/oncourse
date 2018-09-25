package ish.oncourse.webservices.soap.interceptors;

import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.exception.AuthSoapFault;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.tapestry5.services.Request;
import org.junit.Test;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageHeaderInterceptorTest {

    private static final String ESCAPED_SECURITY_CODE = "tLBi`t9te&lt;&gt;$3`vFb";
    private static final String EXPECTED_SECURITY_CODE = "tLBi`t9te<>$3`vFb";
    private static final String SECURITY_CODE_QNAME = "SecurityCode";

    @Test
    public void test() throws Exception {
        Header header = prepareSecurityCodeHeader(ESCAPED_SECURITY_CODE);
        QName qName = new QName(SECURITY_CODE_QNAME);

        SoapMessage soapMessage = mock(SoapMessage.class);
        when(soapMessage.getHeader(qName)).thenReturn(header);

        Request request = mock(Request.class);
        ICollegeService collegeService = mock(ICollegeService.class);
        when(collegeService.findBySecurityCode(SECURITY_CODE_QNAME)).thenReturn(null);

        MessageHeaderInterceptor interceptor = new MessageHeaderInterceptor();
        forceSet(interceptor, "request", request);

        try
        {
            interceptor.handleMessage(soapMessage);
            Element el = (Element) soapMessage.getHeader(qName).getObject();
            assertEquals(EXPECTED_SECURITY_CODE, el.getTextContent());
        }
        catch (AuthSoapFault e)
        {
            assertNotNull(e);
        }
    }

    private void forceSet(MessageHeaderInterceptor interceptor, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = interceptor.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(interceptor, value);
    }

    private Header prepareSecurityCodeHeader(String securityCodeContent) {
        Element element = mock(Element.class);
        when(element.getTextContent()).thenReturn(securityCodeContent);

        Header header = new Header(new QName(SECURITY_CODE_QNAME), element);
        return header;
    }
}
