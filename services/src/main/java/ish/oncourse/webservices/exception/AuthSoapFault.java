package ish.oncourse.webservices.exception;

import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapFault;

@SuppressWarnings("serial")
public class AuthSoapFault extends SoapFault {

	public AuthSoapFault(String message) {
		super(message, new QName("http://auth.v4.soap.webservices.oncourse.ish/"));
	}
}
