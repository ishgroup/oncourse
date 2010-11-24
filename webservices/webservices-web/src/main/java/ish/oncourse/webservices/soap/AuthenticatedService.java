package ish.oncourse.webservices.soap;

import javax.jws.WebService;


/**
 * Service responsible for the authentication of the Angel system.
 *
 * @author Marek Wawrzyczny
 */
@WebService
public interface AuthenticatedService {

	/**
	 * Authenticates user, stores details in HTTP Session and returns a token 
	 * to be used in future communication between the systems.
	 * 
	 * @param securityCode code generated/stored within Angel database
	 * @return ReplicationToken to be used in future Angel requests
	 */
	ReplicationToken authenticate(String securityCode)
			throws AuthenticationException;

	/**
	 * End the session on Willow - this will discard the HTTP Session and
	 * invalidate the ReplicationToken passed.
	 *
	 * @param sessionToken
	 */
	void logout(ReplicationToken sessionToken);
	
}
