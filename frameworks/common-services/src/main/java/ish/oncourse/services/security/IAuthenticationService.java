package ish.oncourse.services.security;

import ish.oncourse.model.WillowUser;


public interface IAuthenticationService {
	
	/**
	 * Cookie to identify user is using the CMS. Do not change as the string
	 * literal is used by Apache rewrite rules!
	 */
	public static final String CMS_COOKIE_NAME = "cms";
	

	/** CMS Cookie timeout value. */
	public static final int CMS_COOKIE_AGE = 3600;
	
	/**
	 * Performs authentication based of the given username/password. Returns
	 * authentication processing status. If a user is successfully
	 * authenticated, a corresponding WillowUser object is stored in the session
	 * behind the scenes, so subsequent calls to {@link #getUser()} wil return
	 * it.
	 *
	 * @param userName
	 * @param password
	 * @return Authentication status
	 */
	AuthenticationStatus authenticate(String userName, String password);


	/**
	 * Returns current session user for authenticated sessions, or null for
	 * unauthenticated sessions.
	 */
	WillowUser getUser();
	
	/**
	 * Performs logout, cleanups cookies, invalidates http session.
	 */
	void logout();
}
