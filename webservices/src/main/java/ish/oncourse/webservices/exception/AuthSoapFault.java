package ish.oncourse.webservices.exception;

import java.util.ResourceBundle;

import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.common.i18n.Message;

public class AuthSoapFault extends SoapFault {

	public AuthSoapFault(String key, Object... params) {
		super(new Message(key, ResourceBundle.getBundle("fault"), params), new QName("http://auth.v4.soap.webservices.oncourse.ish/", key));
	}
}
