/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.soap.v4.auth;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.ws.WebFault;

/**
 * 
 * @author marek
 */
@WebFault(name = "AuthenticationFailure")
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthenticationFailureException extends Exception {

	public static final Integer INVALID_SECURITY_CODE = 1;
	public static final Integer INVALID_COMMUNICATION_KEY = 2;
	public static final Integer INVALID_SESSION = 3;
	public static final Integer COMMUNICATION_KEY_HALTED = 4;
	public static final Integer NULL_AS_VALID_KEY = 5;

	private Integer status;

	public AuthenticationFailureException() {
		super();
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public AuthenticationFailureException(String message, Integer status) {
		super(message);
		this.status = status;
	}
}
