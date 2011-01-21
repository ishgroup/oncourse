/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.soap.v4.auth;

import java.util.ResourceBundle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.ws.WebFault;

import org.apache.cxf.common.i18n.Message;
import org.apache.cxf.interceptor.Fault;

/**
 * 
 * @author marek
 */
@WebFault(name = "AuthenticationFailure")
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthenticationFailureException extends Fault {

	public AuthenticationFailureException(String key, Object... params) {
		super(new Message(key, ResourceBundle.getBundle("messages"), params));
	}
}
