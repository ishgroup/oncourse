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
	 * Authenticates user, stores details in HTTP Session.
	 * 
	 * @param securityCode code generated/stored within Angel database
	 */
	void authenticate(String securityCode)
			throws AuthenticationException;

	/**
	 * End the session on Willow - this will discard the HTTP Session.
	 */
	void logout();
	
}
