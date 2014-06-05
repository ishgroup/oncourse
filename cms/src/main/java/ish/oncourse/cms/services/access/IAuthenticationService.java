package ish.oncourse.cms.services.access;

import ish.oncourse.model.SystemUser;
import ish.oncourse.model.WillowUser;
import ish.oncourse.services.access.AuthenticationStatus;


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
	 * Returns current session willow user for authenticated sessions, or null for
	 * unauthenticated sessions.
	 */
	WillowUser getUser();
	
	/**
	 * Returns current session system user for authenticated sessions, or null for
	 * unauthenticated sessions.
	 */
	SystemUser getSystemUser();
	
	/**
	 * Performs logout, cleanups cookies, invalidates http session.
	 */
	void logout();

	/**
	 * Returns email of {@link SystemUser} or {@link WillowUser} currently logged in.
	 */
	String getUserEmail();
}
