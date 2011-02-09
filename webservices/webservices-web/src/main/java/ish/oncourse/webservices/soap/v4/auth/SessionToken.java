package ish.oncourse.webservices.soap.v4.auth;

import ish.oncourse.model.College;

import java.io.Serializable;

public class SessionToken implements Serializable {

    public final static String SESSION_TOKEN_KEY = "ish_session_token";
    
	private College college;
	
	public SessionToken(College college) {
		super();
		this.college = college;
	}
	
	/**
	 * @return the college
	 */
	public College getCollege() {
		return college;
	}

	/**
	 * @param college the college to set
	 */
	public void setCollege(College college) {
		this.college = college;
	}
}
