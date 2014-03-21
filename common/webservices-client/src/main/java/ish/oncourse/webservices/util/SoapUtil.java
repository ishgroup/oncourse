package ish.oncourse.webservices.util;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class SoapUtil {
	
	private static final String SECURITY_CODE_HEADER = "SecurityCode";
	private static final String COMMUNICATION_KEY_HEADER = "CommunicationKey";
	private static final String ANGEL_VERSION_HEADER = "AngelVersion";

	private static String getHeader(SoapMessage message, String localName) {
		QName qName = new QName(localName);

		Header hr = message.getHeader(qName);

		if (hr != null) {
			Element el = (Element) hr.getObject();
			return el.getTextContent();
		}

		return null;
	}

	private static void addHeader(SoapMessage message, String localName, String value) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			Element elem = doc.createElement(localName);
			elem.setTextContent(value);

			Header header = new Header(new QName(localName), elem);
			message.getHeaders().add(header);

		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public static void addAngelVersion(SoapMessage message, String angelVersion) {
		addHeader(message, ANGEL_VERSION_HEADER, angelVersion);
	}

	public static String getAngelVersion(SoapMessage message) {
		return getHeader(message, ANGEL_VERSION_HEADER);
	}

	public static void addCommunicationKey(SoapMessage message, Long communicationKey) {
		addHeader(message, COMMUNICATION_KEY_HEADER, String.valueOf(communicationKey));
	}

	public static String getCommunicationKey(SoapMessage message) {
		return getHeader(message, COMMUNICATION_KEY_HEADER);
	}

	public static String getSecurityCode(SoapMessage message) {
		return getHeader(message, SECURITY_CODE_HEADER);
	}

	public static void addSecurityCode(SoapMessage message, String securityCode) {
		addHeader(message, SECURITY_CODE_HEADER, securityCode);
	}
}
