package ish.oncourse.webservices.soap.v4.auth;

import ish.oncourse.model.College;

public class SessionToken {
	
	public static final String SESSION_TOKEN_KEY = "session_token_key";
	
	private College college;
	private Long communicationKey;
	
	public SessionToken(College college, Long communicationKey) {
		this.college = college;
		this.communicationKey = communicationKey;
	}

	public College getCollege() {
		return college;
	}

	public void setCollege(College college) {
		this.college = college;
	}

	public Long getCommunicationKey() {
		return communicationKey;
	}

	public void setCommunicationKey(Long communicationKey) {
		this.communicationKey = communicationKey;
	}
}
