package ish.oncourse.webservices.soap.auth;

import java.io.Serializable;

public class SessionToken implements Serializable {

    final static String SESSION_TOKEN_KEY = "ish_session_token";
	
	private String securityCode;
	private Long communicationKey;
	
	public SessionToken(String securityCode, Long communicationKey) {
		super();
		this.securityCode = securityCode;
		this.communicationKey = communicationKey;
	}
	
	public String getSecurityCode() {
		return securityCode;
	}
	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
	public Long getCommunicationKey() {
		return communicationKey;
	}
	public void setCommunicationKey(Long communicationKey) {
		this.communicationKey = communicationKey;
	}

}
