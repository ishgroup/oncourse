package ish.oncourse.cms.services.security;

import ish.oncourse.model.WillowUser;


public interface IAuthenticationService {

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
}
