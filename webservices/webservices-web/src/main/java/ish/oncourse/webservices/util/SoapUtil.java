package ish.oncourse.webservices.util;

import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.w3c.dom.Element;

public class SoapUtil {

	private SoapUtil() {

	}

	public static final String SECURITY_CODE_HEADER = "SecurityCode";
	public static final String COMMUNICATION_KEY_HEADER = "CommunicationKey";
	public static final String ANGEL_VERSION_HEADER = "AngelVersion";
	
	public static final String DEFAULT_NAMESPACE = "http://ish.com.au";

	public static String getHeader(SoapMessage message, String localName) {
		QName qName = new QName(DEFAULT_NAMESPACE, localName);

		Header hr = message.getHeader(qName);

		if (hr != null) {
			Element el = (Element) hr.getObject();
			return el.getTextContent();
		}

		return null;
	}
}
