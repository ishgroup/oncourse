package ish.oncourse.webservices.exception;

import java.util.ResourceBundle;

import org.apache.cxf.common.i18n.Message;
import org.apache.cxf.interceptor.Fault;

public class AuthFault extends Fault {
	private static final long serialVersionUID = -8993538811784984198L;

	public AuthFault(String key, Object... params) {
		super(new Message(key, ResourceBundle.getBundle("fault"), params));
	}
}
